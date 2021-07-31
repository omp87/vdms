import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class VdmsTransaction
{
    byte size[];
    byte buffer[];
    int messageId;
    int threadId;
    long timestamp;
    Boolean emptyTransactionFlag;

    
    public VdmsTransaction(byte[] nSize, byte[] nBuffer)
    {
        emptyTransactionFlag = true;
        if(nBuffer.length > 0)
        {
            emptyTransactionFlag = false;
        }
        size = nSize;
        buffer = nBuffer;
        messageId = -1;
        timestamp = 0;
    }
    public VdmsTransaction(int nSize, byte[] nBuffer)
    {
        emptyTransactionFlag = true;
        if(nBuffer.length > 0)
        {
            emptyTransactionFlag = false;
        }
        size = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(nSize).array();
        buffer = nBuffer;
        messageId = -1;
        timestamp = 0;
    }

    public byte[] GetSize()
    {
    	return size;
    }

    public Boolean GetEmptyTransactionFlag()
    {
        return emptyTransactionFlag;
    }

    public byte[] GetBuffer()
    {
	    return buffer;
    }

    public int GetMessageId()
    {
        return messageId;
    }

    public void SetMessageId(int nId)
    {
        messageId = nId;
    }

    public void SetThreadId(int nId)
    {
        threadId = nId;
    }

    public int GetThreadId()
    {
        return threadId;
    }

    public long GetTimestamp()
    {
        return timestamp;
    }

    public void SetTimestamp(long nTimestamp)
    {
        timestamp = nTimestamp;
    }

}
