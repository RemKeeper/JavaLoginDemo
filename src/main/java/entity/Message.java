package entity;

import java.sql.Timestamp;

public  class Message {
    private int messageId;
    private String sendName;
    private int sendUserId;
    private String receiveUserName;
    private int receiveUserId;
    private String messageRow;
    private Timestamp sendTime;
    private int isDelete;

    public Message(int messageId, String sendName, int sendUserId, String receiveUserName, int receiveUserId, String messageRow, Timestamp sendTime, int isDelete) {
        this.messageId = messageId;
        this.sendName = sendName;
        this.sendUserId = sendUserId;
        this.receiveUserName = receiveUserName;
        this.receiveUserId = receiveUserId;
        this.messageRow = messageRow;
        this.sendTime = sendTime;
        this.isDelete = isDelete;
    }
    public int getMessageId() {
        return messageId;
    }

    public String getSendName() {
        return sendName;
    }

    public int getSendUserId() {
        return sendUserId;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public int getReceiveUserId() {
        return receiveUserId;
    }

    public String getMessageRow() {
        return messageRow;
    }

    public Timestamp getSendTime() {
        return sendTime;
    }

    public int getIsDelete() {
        return isDelete;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", sendName='" + sendName + '\'' +
                ", sendUserId=" + sendUserId +
                ", receiveUserName='" + receiveUserName + '\'' +
                ", receiveUserId=" + receiveUserId +
                ", messageRow='" + messageRow + '\'' +
                ", sendTime=" + sendTime +
                ", isDelete=" + isDelete +
                '}';
    }
}
