import java.util.ArrayList;
import java.util.List;

/**
* A class holding transactions between a VDMS client and a VDMS server. A basic transaction includes a 4-byte array holding the size and a buffer holding a protobuf with the actual transaction.
*/
class VdmsTransaction
{
    byte size[]; /**< buffer that contains the size of data that will be transmitted with VDMS protobuf */ 
    byte buffer[]; /**< buffer holding the VDMS protobuf */
    int messageId; /**< id assigned to message  */  
    int threadId; /**< threadId corresponding with sender of this message */  
    
    
    /**
    * a constructor to create a new VDMS transaction. The messageId and timestamp receive non-valid numbers and will be set by other functions
    * @param nSize size of protobuf transaction
    * @param nBuffer buffer holding protobuf with VDMS Transaction
    */
    public VdmsTransaction(byte[] nSize, byte nBuffer[], int nThreadId, int nMessageId)
    {
        size = nSize;
        buffer = nBuffer;
        threadId = nThreadId;
        messageId = nMessageId;
    }
    
    /**
    * function to get size of the VDMS transaction protobuf 
    * @return byte array that holds the size of the VDMS database transaction
    */    
    public byte[] GetSize()
    {
        return size;
    }
    
    /**
    * function to get the contents of the protobuf containing the VDMS database transaction 
    * @return byte array that holds the protobuf content of VDMS transaction
    */    
    public byte[] GetBuffer()
    {
        return buffer;
    }
    
    /**
    * function to get the id of the message
    * @see SetMessageId()
    * @return ID of the current message - incremental value that the plugin has received
    */
    public int GetMessageId()
    {
        return messageId;
    }
    
    /**
    * function to set the id of the current transaction
    * @param nId new ID  corresponding with thread that created this transaction
    * @see GetMessageId()
    * @return void()
    */
    public void SetMessageId(int nMessageId)
    {
        messageId = nMessageId;
    }
    
    /**
    * function to get the id of the thread that originating this transaction
    * @see SetThreadId()
    * @return timestamp corresponding to time at which this tranaction was created
    */
    public int GetThreadId()
    {
        return threadId;
    }
    
    /**
    * function to set the timestamp of new transaction with a new value
    * @param nId new ID  corresponding with thread that created this transaction
    * @see GetThreadId()
    * @return void()
    */
    public void SetThreadId(int nThreadId)
    {
        threadId = nThreadId;
    }
    
}
