import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class BASIC {
    private Map<String, Integer> dataMap = new HashMap<String, Integer>();
    private List<Word> data = new ArrayList<Word>();

    private class Word {
        private String word;
        private int freq;

        public Word(String word, int freq) {
            this.word = word;
            this.freq = freq;
        }
    }

    // Read file and store the frequencies attached to each word appeared
    private void readFile(String dir) {
        try {
            File[] files = new File(dir).listFiles();
            BufferedReader br;
            for (File file : files) {
                br = new BufferedReader(new FileReader(file));
                String strLine = null;
                while ((strLine = br.readLine()) != null) {
                    strLine = strLine.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "");
                    if (strLine.length() == 0) {
                        continue;
                    }
                    final String[] words = strLine.split(" ");
                    for (String word : words) {
                        if (dataMap.get(word) != null) {
                            dataMap.put(word, dataMap.get(word) + 1);
                        } else {
                            dataMap.put(word, 1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<Map.Entry<String, Integer>> iter = dataMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Integer> entry = iter.next();
            data.add(new Word(entry.getKey(), entry.getValue().intValue()));
        }
        System.out.println("basic: finish reading files");
    }

    // Sort by frequencies (prior) and strings
    private Stack<Record> stack = new Stack<>();

    public void sort() {
        if (1 < data.size()) {
            stack.push(new Record(0, data.size() - 1));
            int pivot;
            while (!stack.isEmpty()) {
                Record record = stack.pop();
                pivot = partition(record.left, record.right);
                if (pivot - 1 >= record.left) {
                    stack.push(new Record(record.left, pivot - 1));
                }
                if (pivot + 1 <= record.right) {
                    stack.push(new Record(pivot + 1, record.right));
                }
            }
        }
        System.out.println("basic: finish sorting");
    }

    private static class Record {
        int left;
        int right;
 
        private Record(int left, int right) {
            this.left = left;
            this.right = right;
        }
    }
    
    // Mono-direction partition: left -> right
    private int partition(int low, int high) {
        int i = low;
        Word tmp;
        for (int j = low + 1; j <= high; j++) {
            if (wordCmp(j, low) == true) {
                i++;
                if (i != j) {
                    tmp = data.get(j);
                    data.set(j, data.get(i));
                    data.set(i, tmp);
                }
            }
        }
        tmp = data.get(low);
        data.set(low, data.get(i));
        data.set(i, tmp);
        return i;
    }

    // compare two words' priority, return true : data[i] has higher priority
    private boolean wordCmp(int i, int j) {
        Word w1 = data.get(i);
        Word w2 = data.get(j);
        if (w1.freq > w2.freq) {
            return true;
        } else if (w1.freq == w2.freq && w1.word.compareTo(w2.word) < 0) {
            return true;
        }
        return false;
    }

    // Display all words and corresponding frequencies
    private void output() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("result.txt").getAbsolutePath()));
            for (Word w : data) {
                bw.write(w.word + '\t' + w.freq + '\n');
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BASIC basic = new BASIC();
        System.out.println(new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
        basic.readFile(args[0]);
        basic.sort();
        basic.output();
        System.out.println(new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
    }
}
