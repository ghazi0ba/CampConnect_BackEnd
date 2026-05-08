package com.example.campconnect_backend.Dto;



public class AiChatDto {

    public static class Request {
        private String message;

        public Request() {}

        public Request(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Response {
        private String response;

        public Response() {}

        public Response(String response) {
            this.response = response;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }
}
