import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SLAVE {
    String id;
    private Map<String, Integer> dataMap = new HashMap<String, Integer>();

    private SLAVE(String id) {
        this.id = id;
    }

    private void map() {
        try {
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File("SM/SM1"+id+".txt").getAbsolutePath()));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File("SM/SM2"+id+".txt").getAbsolutePath()));
            BufferedWriter bw3 = new BufferedWriter(new FileWriter(new File("SM/SM3"+id+".txt").getAbsolutePath()));
            BufferedReader br = new BufferedReader(new FileReader(new File("Sx"+id+".txt")));
            String strLine = null;
            while((strLine = br.readLine()) != null) {
                String[] words = strLine.split(" ");
                for (String word : words) {
                    if (word.compareTo("R") < 0) {
                        bw1.write(word + '\n');
                    } else if (word.compareTo("i") < 0) {
                        bw2.write(word + '\n');
                    } else {
                        bw3.write(word + '\n');
                    }
                }
            }
            bw1.close();
            bw2.close();
            bw3.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("worker" + this.id + ": finish mapping");
    }

    private void transferSM() {
        try {
            Process p1 = new ProcessBuilder("scp", "SM/SM1"+id+".txt", "worker1:/tmp/SS").start();
            Process p2 = new ProcessBuilder("scp", "SM/SM2"+id+".txt", "worker2:/tmp/SS").start();
            Process p3 = new ProcessBuilder("scp", "SM/SM3"+id+".txt", "worker3:/tmp/SS").start();
            p1.waitFor();
            p2.waitFor();
            p3.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("worker" + this.id + ": finish transferring SM");
    }

    private void reduceSM() {
        try {
            File[] files = new File("/tmp/SS").listFiles();
            BufferedReader br;
            for (File file : files) {
                if (file.getName().equals("RM"+id+".txt")) {
                    continue;
                }
                br = new BufferedReader(new FileReader(file));
                String word = null;
                while ((word = br.readLine()) != null) {
                    if (dataMap.get(word) != null) {
                        dataMap.put(word, dataMap.get(word) + 1);
                    } else {
                        dataMap.put(word, 1);
                    }
                }
            }
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("SS/RM"+id+".txt").getAbsolutePath()));
            Iterator<Map.Entry<String, Integer>> iter = dataMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Integer> entry = iter.next();
                bw.write(entry.getKey() + ' ' + Integer.toString(entry.getValue()) + '\n');
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("worker" + this.id + ": finish reducing");
    }

    public static void main(String[] args) {
        SLAVE worker = new SLAVE(args[0]);
        if (args[1].equals("0")) {
            System.out.println("test mode");
        } else if (args[1].equals("1")) {
            worker.map();
            worker.transferSM();
        } else if (args[1].equals("2")) {
            worker.reduceSM();
        }
    }
}