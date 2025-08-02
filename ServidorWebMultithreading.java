import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class ServidorWebMultithreading{
    public static void main(String[] args) throws Exception {
        
        try {

            //Creation of the port
            ServerSocket listenSocket = new ServerSocket(6789);
            
            System.out.println("The server is ready to accept connections on the port: 6789.");

            while(true){

                //Accept the connection
                Socket socketOfConection = listenSocket.accept();

                HttpRequest request = new HttpRequest(socketOfConection);

                //Thread creation
                Thread thread = new Thread(request);

                thread.start();
                
            }
            
        } catch (Exception e) {
            System.out.println("Error starting the server: " + e.getMessage());
        }
        
        
    }
    
}

final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";

    Socket socket;

    //Constructor 
    public HttpRequest(Socket socket) throws Exception{
        this.socket = socket;
    }

    @Override
    //The run method of the interface Runnable
    public void run() {
        try {
            requestProcess();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
    
    private void requestProcess() throws Exception {

        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String lineOfRequest = br.readLine();
        
        System.out.println("");
        System.out.println(lineOfRequest);

        //Extract the name of the file of the line of request
        StringTokenizer partsLine = new StringTokenizer(lineOfRequest);
        
        partsLine.nextToken(); //Goes to the next token it's suppose to be GET 
        String fileName = partsLine.nextToken();

        //We are gonna put a '.' on that way the resquest file should be in the same directory
        fileName = "." + fileName;

        String headerLine = null;

        //Open the selected file
        FileInputStream fis = null;
        boolean fileExist = true;

        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExist = false;
        }

        while((headerLine = br.readLine()).length() != 0){
            System.out.println(headerLine);
        }

        //Construye un mensaje de respuesta 
        String lineOfState = null;
        String lineTypeOfContent = null;
        String messageBody = null;

        if(fileExist){
            lineOfState = "HTTP/1.1 200 OK" + CRLF;
            lineTypeOfContent = "Content-type: " + contentType(fileName) + CRLF;
        }else{
            lineOfState = "HTTP/1.1 404 Not Found" + CRLF;
            lineTypeOfContent = "Content-type: text/html" + CRLF;
            messageBody = "<HTML>" + "<HEAD><TITLE>404 Not Found</TITLE></HEAD>"+"<BODY><b>404</b>Not Found</BODY></HTML>";
        }

        //Send the state line 
        os.writeBytes(lineOfState);
        
        //Send the content of the line content-type 
        os.writeBytes(lineTypeOfContent);

        //Send a blank line meaning the final of the lines of the header
        os.writeBytes(CRLF);

        //Send the body of the message
        if (fileExist) {

            sendBytes(fis,os);
            fis.close();

        } else {
            os.writeBytes(messageBody);
        }

        //End the Thread and close the streams
        os.close();
        br.close();
        socket.close();

    }

    //Send the Bytes
    private void sendBytes(FileInputStream fis, OutputStream os){

        //Build a buffer of 1KB to save the bytes when they go to the socket
        byte[] buffer = new byte[1024];
        
        int bytes = 0;  

        //Copy the file requested to the output stream of the socket 
        try {
            while ((bytes = fis.read(buffer)) != -1) {
            
                try {
                    os.write(buffer, 0, bytes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private static String contentType(String fileName) {

        if(fileName.endsWith(".htm") || fileName.endsWith(".html")){
            return "text/html";
        }
        if(fileName.endsWith(".jpg")){
            return ("image/jpeg");
        }
        if(fileName.endsWith(".gif")){
            return("image/gif");
        }
        
        return "application/octet-stream";

    }
}
