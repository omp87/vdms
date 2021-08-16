import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

class ClientServiceThread extends Thread
{ 
    Socket myClientSocket; /**< socket of the incoming connection from a client */
    boolean m_bRunThread = true; /**< Boolean flag indicating whether the current thread is valid */
    int id; /**< id of the thread assigned - this is not the same as the ThreadId automatically assigned by the application */
    int type; /**< Flag indicating whether this thread is a producer or a consumer */
    int messageId; /**< id of the message that is being created */
    TestServer manager; /**< pointer to the manager or producer */
    BlockingQueue<VdmsTransaction> responseQueue; /**< queue from which this thread should take data that should be sent to the appropriate location */
    
    /**
    * A constructor for a thread to handle incoming connections from clients. This empty constructor is only used when the object is initialized after it is created 
    */
    public ClientServiceThread()
    { 
        super();
        responseQueue = null;
    } 
    
    /**
    * a constructor that creates a thread to handle incoming connections from clients
    * @param nManager pointer to the manager that is handling the incoming manager
    * @param s socket that connectes the  client and the server
    * @param nThreadId Id of the thread - this value is assigned by application code and not the automatic id created by OS
    */
    ClientServiceThread(TestServer nManager, Socket s, int nThreadId) 
    {
        responseQueue = new ArrayBlockingQueue<VdmsTransaction>(128);
        manager = nManager;
        myClientSocket = s;
        id = nThreadId;
        type = -1;
        messageId = 0;
    } 
    
    /**
    * get flag indicating whether this thread corresponds with a consumer (manager) or producer (client)
    * @return Integer where 0 indicates this is a producer and 1 indicates this is a consumer
    */
    public int GetType()
    {
        return type;
    }
    
    /**
    * get the thread id of the thread
    * @return Integer coresponding with the thread 
    */
    public int GetId()
    {
        return id;
    }
    
    /**
    * publish message to appropriate recipients
    * @param nMessage message that should be passed to connecting socket
    * @return void()
    */
    public void Publish(VdmsTransaction nMessage)
    {
        responseQueue.add(nMessage);
    }
    
    /**
    * a loop to handle the client connections. This function is repsonsible for handling connections for both producers (managers) and consumers (clients). This function will wait for a message in the service queue. Once that message arrives, it will be placed into the appropriate queue and then published to the proper recipients.
    */
    public void run() 
    { 
        DataInputStream in = null; 
        DataOutputStream out = null;
        byte[] readSizeArray = new byte[4];
        byte[] messageIdArray = new byte[4];
        byte[] threadIdArray = new byte[4];
        int bytesRead;
        int readSize;
        int returnedThreadId;
        int returnedMessageId;
        VdmsTransaction returnedMessage;
        VdmsTransaction newTransaction;
        
        try
        { 
            in = new DataInputStream(myClientSocket.getInputStream());
            out = new DataOutputStream(myClientSocket.getOutputStream());
            
            while(m_bRunThread) 
            {
                bytesRead = in.read(readSizeArray, 0, 4);
                readSize =  ByteBuffer.wrap(readSizeArray).order(ByteOrder.LITTLE_ENDIAN).getInt();
                //now i can read the rest of the data
                //System.out.println("readsizearray - " + Arrays.toString(readSizeArray) + Integer.toString(readSize));		
                
                //if we have not determined if this node is a producer or consumer
                if(type == -1)
                {                
                    //Producer from host that are unaware that this is a node mimicking vdms
                    if(readSize > 0)
                    {
                        type = 0;
                        manager.AddNewProducer(this);
                    }
                    //Consumer that are aware this is a a node mimimicking vdms and listening for messages
                    else
                    {
                        type = 1;
                        manager.AddNewConsumer(this);
                        readSize = -1 * readSize;
                    }
                }
                
                byte[] buffer = new byte[readSize];
                
                int totalBytesRead = 0;
                while(totalBytesRead < readSize)
                {
                    bytesRead = in.read(buffer, totalBytesRead, readSize-totalBytesRead);
                    totalBytesRead += bytesRead;
                    //System.out.println("buffer - " + Arrays.toString(buffer));
                }
                
                if(type == 0)
                {
                    //if type is producer - put the data in out queue and then wait for data in the in queue                    
                    newTransaction = new VdmsTransaction(readSizeArray, buffer, id, messageId);
                    String tmpString = new String(buffer, StandardCharsets.UTF_8);
                    //System.out.println(tmpString);
                    manager.AddToConsumerQueue(newTransaction);
                    returnedMessage = responseQueue.take();
                    out.write(returnedMessage.GetSize());
                    out.write(returnedMessage.GetBuffer());
                    ++messageId;  
                }
                //if type is producer - put the data in out queue and then wait for data in the in queue
                else
                {
                    //first message from consumer nodes does not have data that should be sent to the producers
                    //bust still need to wait for a message to go into response queue before proceeding
                    if(messageId > 0)
                    {
                        returnedMessageId =  in.readInt();
                        returnedThreadId =  in.readInt();
                        //System.out.println(returnedThreadId); //debugging does not work unless this line is present
                        String tmpString = new String(buffer, StandardCharsets.UTF_8);
                        //System.out.println(tmpString);
                        newTransaction = new VdmsTransaction(readSizeArray, buffer, returnedThreadId, returnedMessageId);
                        manager.AddToProducerQueue(newTransaction);
                    }
                    //Data is sent to plugin. In addition to the basic VDMS transaction, also additional data for performance measurements
                    returnedMessage = responseQueue.take();
                    out.write(returnedMessage.GetSize());
                    out.write(returnedMessage.GetBuffer());
                    out.writeInt(returnedMessage.GetMessageId());
                    out.writeInt(returnedMessage.GetThreadId());
                    ++messageId;
                }
                
                if(!manager.GetServerOn()) 
                { 
                    System.out.println("Server stopped");
                    out.flush(); 
                    m_bRunThread = false;
                } 
                else if(bytesRead == -1) 
                {
                    m_bRunThread = false;
                    manager.SetServerOn(false);
                } 
                else 
                {
                    out.flush(); 
                } 	
            } 
        } 
        catch(Exception e) 
        { 
            //e.printStackTrace();
            //Exception here could indicate that the socket was closed
        } 
        finally 
        { 
            try
            { 
                in.close(); 
                out.close(); 
                myClientSocket.close();
                //future - check type and remove from the approrpriate list
                System.out.println("Socket Connection Closed"); 
            }
            catch(IOException ioe)
            { 
                ioe.printStackTrace(); 
            } 
        } 
    } 
}
