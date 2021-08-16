import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.ArrayList;
import java.util.List;

/**
* A class to handle queueing data going from a producer to a consumer and back from a consumer to a producer. Messages are passed into the BlockingQueue queue. Once a message arrives in a queue, it is forwarded to the appropriate destinations
*/
class QueueServiceThread extends Thread 
{ 
   BlockingQueue<VdmsTransaction> queue; /**< queue to hold messages to be provided to appropriate source*/
   TestServer manager; /**< manager related to this message queue. If the queue belongs to a producer then this points to the producer. If this queue belongs to a consumer, then this queue points to the producer from which this consumer receives messages. */
   int matchType; /**< flag indicating whether this queue belongs to a producer or a consumer */
   
   /**
   * constructor to create a QueueServiceThread object. 
   * @param nQueue pointer to the queue created for this transacatuon
   * @param nManager a pointer to the manager or server
   * @param nMatchType flag indicating whether this queue belongs to a producer or consumer
   */
   public QueueServiceThread(BlockingQueue<VdmsTransaction> nQueue, TestServer nManager, int nMatchType)
   {
      queue = nQueue;
      manager = nManager;
      matchType = nMatchType;
   }
   
   
   /**
   * control loop that handles the control of the QueueServiceThread. This funcion continuously waits for a new message and then determines the appropriate destination for an arriving message. Currently all messages are forwarded to all consumers. Messages are only returned to the producer matching one Id (the originating id of the database transaction.)
   */
   public void run()
   {
      VdmsTransaction message;
      List<ClientServiceThread> publishList;  
      try
      {
         while(true)
         {
            message = queue.take();
            
            //Get any new publishers that may exist
            if(matchType == 0)
            {
               publishList = manager.GetConsumerList();
               //Publish to all of the associated threads
               for(int i = 0; i < publishList.size(); i++)
               {
                  publishList.get(i).Publish(message);
               }               
            }
            else
            {
               //Before sending data, get a list of any potential new Producers
               publishList = manager.GetProducerList();
               ClientServiceThread thisClient;
               //future - change this code to have constant lookup - pass the index of array with pointer to thread
               for(int i = 0; i < publishList.size(); i++)
               {
                  thisClient = publishList.get(i);
                  if(thisClient.GetId() == message.GetThreadId())
                  {
                     thisClient.Publish(message);                     
                  }
               }
            }
         }         
      }
      catch(InterruptedException e)
      {
         this.interrupt();
      }
   }   
}
