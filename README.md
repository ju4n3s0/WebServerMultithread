# WebServerMultithread
This is a basic server with supporting for multiple simultaneous connections using Java

Characteristics:
- Listens for incoming HTTP connections on port 6789.
- Supports multiple clients concurrently using Java threads.
- Responds to HTTP GET requests by serving static HTML files.
- Implements basic HTTP response handling, including 200 OK and 404 Not Found.

Technologies:
- Java 17+.
- Sockets (ServerSocket, Socket)
- Multithreading (Thread, Runnable)
- Buffered I/O (BufferedReader, BufferedWriter)

How to Run?:
1. Compile the Java files:
javac ServidorWebMultithreading.java ManejadorCliente.java

Run the server:
java ServidorWebMultithreading

Open your browser and go to:
http://localhost:6789/miarchivo.html
