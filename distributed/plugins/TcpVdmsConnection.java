import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class TcpVdmsConnection extends VdmsConnection 
{
    Socket socket; /**< socket to use to communicate  */  
    DataInputStream in; /**< input data stream connecting to socket */  
    DataOutputStream out; /**< output data stream connecting to socket */  
    
    /**
    * constructor to create a structure with a TCP connection and the input/output streams
    * @param initString string containing jsaon values that contains information needed to initialize the TCP connection
    * @see publicVar()
    * @return new TcpVdmsConnection
    */
    public TcpVdmsConnection(String initString)
    {
        super(initString);
        try
        {
            socket = new Socket(hostName, hostPort);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        }
        catch(UnknownHostException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    /**
    * function that closes the tcp connection and terminates the input and output data streams
    * @see TcpVdmsConnection()
    * @return void()
    */    
    public void Close()
    {
        try 
        {
            in.close();
            out.close();
            socket.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("Socket was closed\n");
        }        
    }
    
    public void WriteInitMessage()
    {
        try
        {
            out.write(initSequence.GetSize());
            out.write(initSequence.GetBuffer());
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(-1); 
        }
    }
    
    public void Write(VdmsTransaction outMessage)
    {
        try
        {    
            out.write(outMessage.GetSize());
            out.write(outMessage.GetBuffer());
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    public void WriteExtended(VdmsTransaction outMessage)
    {
        Write(outMessage);
        try
        {
            out.writeInt(outMessage.GetMessageId());
            out.writeInt(outMessage.GetThreadId());
            //System.out.println("write extended " + String.valueOf(outMessage.GetThreadId()));
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        } 
    }

    
    
    public VdmsTransaction Read()
    {
        byte[] readSizeArray = new byte[4];
        int readSize;
        VdmsTransaction readValue = null;
        //
        try
        {
            in.read(readSizeArray, 0, 4);
            readSize = ByteBuffer.wrap(readSizeArray).order(ByteOrder.LITTLE_ENDIAN).getInt();
            //now i can read the rest of the data
            //System.out.println("publisher readsizearray - " + Arrays.toString(readSizeArray));		
            
            byte[] buffer = new byte[readSize];
            int totalReadSize = 0;
            while(totalReadSize < readSize)
            {
                int actualReadSize = in.read(buffer, totalReadSize, readSize-totalReadSize);
                totalReadSize += actualReadSize;
            }
            readValue = new VdmsTransaction(readSizeArray, buffer);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        
        return readValue;
    }
    
    /**
    * read the a VDMS transaction in addition to extended information related to database transaction performance
    * @see Read()
    * @return VdmsTransaction - an extened VDMS Transaction that includes the basic message in addition to the the extended information
    */
    public VdmsTransaction ReadExtended()
    {  
        VdmsTransaction readValue = Read();
        try
        {
            int nMessageId = in.readInt();
            int nThreadId = in.readInt();
            readValue.SetMessageId(nMessageId);
            readValue.SetThreadId(nThreadId);
            //System.out.println("read extended " + String.valueOf(nThreadId));
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        return readValue;
    }
}

