package Server;


import command.*;

import spacemarine.Utils;
import spacemarine.Writer;
import Exceptions.EndOfFileException;
import Exceptions.FailedCheckException;


import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.util.Properties;

public class ServerThread {

    private static final Logger logger = LogManager.getLogger(ServerThread.class);
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static final ExecutorService poolFixed = Executors.newFixedThreadPool(3);
    private static Collection collection;
    private static final AtomicBoolean globalKillFlag = new AtomicBoolean(false);
    private static PostgreSQL sqlRun;

    public static void main(String[] args) throws IOException, InterruptedException {
    	
    	Properties properties=new Properties();
        properties.setProperty("log4j.rootLogger","TRACE,stdout,MyFile");
        properties.setProperty("log4j.rootCategory","TRACE");

        properties.setProperty("log4j.appender.stdout",     "org.apache.log4j.ConsoleAppender");
        properties.setProperty("log4j.appender.stdout.layout",  "org.apache.log4j.PatternLayout");
        properties.setProperty("log4j.appender.stdout.layout.ConversionPattern","%d{yyyy/MM/dd HH:mm:ss.SSS} [%5p] %t (%F) - %m%n");

        properties.setProperty("log4j.appender.MyFile", "org.apache.log4j.RollingFileAppender");
        properties.setProperty("log4j.appender.MyFile.File", "my_example.log");
        properties.setProperty("log4j.appender.MyFile.MaxFileSize", "100KB");
        properties.setProperty("log4j.appender.MyFile.MaxBackupIndex", "1");
        properties.setProperty("log4j.appender.MyFile.layout",  "org.apache.log4j.PatternLayout");
        properties.setProperty("log4j.appender.MyFile.layout.ConversionPattern","%d{yyyy/MM/dd HH:mm:ss.SSS} [%5p] %t (%F) - %m%n");

        PropertyConfigurator.configure(properties);

        Logger logger = Logger.getLogger("MyFile");

        
            ServerSocketChannel ssc = ServerSocketChannel.open();
            try {
                ssc.bind(new InetSocketAddress(args[0], Utils.portCheck.checker(Integer.parseInt(args[1]))));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException | FailedCheckException e) {
                ssc.bind(new InetSocketAddress("localhost", 4356));
            }
            ssc.configureBlocking(false);
            Writer.writeln("������ �������.");
            logger.info("������ �������. " + ssc.getLocalAddress());

            sqlRun = new PostgreSQL("URL", "login", "password", globalKillFlag);
            sqlRun.start();
            collection = Collection.start(sqlRun);
            collection.getAll(sqlRun);
           

            new Thread(() -> {
                try {
                    while (!Console.console.read().equals("exit")) {
                        Writer.writeln("����� ������� ���");
                    }
                    globalKillFlag.set(true);
                } catch (EndOfFileException e) {
                    globalKillFlag.set(true);
                    Writer.writeln("������ � �������� ���������.");
                }
            }).start();

            while (!globalKillFlag.get()) {
                SocketChannel s = ssc.accept();
                if (s != null) {
                    System.out.println("���������� � " + s);
                    s.configureBlocking(false);
                    ConcurrentLinkedQueue<ByteBuffer> messages = new ConcurrentLinkedQueue<>();
                    final AtomicBoolean killFlag = new AtomicBoolean(false);
                    //����� ��� ������
                    poolFixed.submit(() -> read(s, messages, killFlag));
                    //����� ��� ������
                    pool.submit(() -> answer(s, messages, killFlag));
                }
                Thread.sleep(500);
                //System.out.println(Thread.activeCount());
            }
            ssc.close();
            logger.info("������ ������");
       
        poolFixed.shutdownNow();
        pool.shutdownNow();
        globalKillFlag.set(true);
    }
    
    public static void reloadCollection() {
    	collection = null;
    	collection = Collection.start(sqlRun);
        collection.getAll(sqlRun);
    }

    private static void read(SocketChannel channel, ConcurrentLinkedQueue<ByteBuffer> messages, AtomicBoolean killFlag) {
        try {
            while (true) {
                ByteBuffer buf = ByteBuffer.allocateDirect(8192);
                int read = channel.read(buf);
                if (killFlag.get() || globalKillFlag.get()) {
                    channel.close();
                    break;
                }
                if (read == -1) {
                    channel.close();
                    throw new IOException();
                } else if (read != 0) {
                    //����� ���������
                    pool.submit(() -> {
                        try {
                            buf.flip();
                            process(buf, messages);
                        } catch (IOException | ClassNotFoundException e) {
                            killFlag.set(true);
                            Writer.writeln("��� ��������� ������� ��������� ������.");
                            Writer.writeln("���� ������������ ���������� ���� �����������.");
                        } catch (EndOfFileException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    });
                }
                Thread.sleep(500);
            }
        } catch (IOException | InterruptedException e) {
            Writer.writeln("���������� ���������...");
            Writer.writeln("������ ��������� ��������. ���������� ��������� ������ ������, ����� ������������ ����������.");
            logger.info("���������� ���������. ������ ��������� ��������.");
        }
        System.out.println("�������� read");
        killFlag.set(true);
    }
    
    private static void answer(SocketChannel s, ConcurrentLinkedQueue<ByteBuffer> messages, AtomicBoolean killFlag) {
        try {
        	reloadCollection();
            while (!(killFlag.get() || globalKillFlag.get())) {
                ByteBuffer buf;
                if (!messages.isEmpty()) {
                    buf = messages.poll();
                    s.write(buf);
                }
                Thread.sleep(500);
            }
        } catch (IOException | InterruptedException e) {
            Writer.writeln("�� ������� ��������� ��� ������.");
        }
        System.out.println("�������� answer");
        killFlag.set(true);
    }

    private static void process(ByteBuffer buf, ConcurrentLinkedQueue<ByteBuffer> messages) throws IOException, ClassNotFoundException, EndOfFileException {
    	reloadCollection();
    	byte[] arr = new byte[buf.remaining()];
        buf.get(arr);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(arr));
        CommandSimple command = (CommandSimple) objectInputStream.readObject();
        objectInputStream.close();
        buf.clear();
        Writer w = CommandConvert.switcher(command, collection, sqlRun);
        reloadCollection();
        Writer.writeln("������� ��������: " + command.getCurrent().toString());
        logger.info("������� ��������: " + command.toString());
        logger.info("�������� ���������� �������. �����:" + (w.toString()));
        


        int number = 0;
        w.shatter();
        Writer subW;

        do {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            subW = w.getSubWriter((number)*10,(number + 1)*10);
            objectOutputStream.writeObject(subW);
            buf = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
            messages.add(buf);
            objectOutputStream.close();
            number++;
        } while (!subW.isEnd());
    }

}