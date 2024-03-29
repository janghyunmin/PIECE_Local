package com.bsstandard.piece.data.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * packageName    : com.bsstandard.piece.data.dto
 * fileName       : EventDTO
 * author         : piecejhm
 * date           : 2022/09/11
 * description    : 이벤트 목록 조회 DTO
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2022/09/11        piecejhm       최초 생성
 */
public class EventDTO {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public class Datum {

        @SerializedName("eventId")
        @Expose
        private String eventId;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("contents")
        @Expose
        private String contents;
        @SerializedName("eventBeginDate")
        @Expose
        private String eventBeginDate;
        @SerializedName("eventEndDate")
        @Expose
        private String eventEndDate;
        @SerializedName("representThumbnailPath")
        @Expose
        private String representThumbnailPath;
        @SerializedName("createdAt")
        @Expose
        private String createdAt;

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }

        public String getEventBeginDate() {
            return eventBeginDate;
        }

        public void setEventBeginDate(String eventBeginDate) {
            this.eventBeginDate = eventBeginDate;
        }

        public String getEventEndDate() {
            return eventEndDate;
        }

        public void setEventEndDate(String eventEndDate) {
            this.eventEndDate = eventEndDate;
        }

        public String getRepresentThumbnailPath() {
            return representThumbnailPath;
        }

        public void setRepresentThumbnailPath(String representThumbnailPath) {
            this.representThumbnailPath = representThumbnailPath;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

    }
}
