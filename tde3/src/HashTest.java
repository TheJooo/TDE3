import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

abstract class HashTable {
    protected List<LinkedList<String>> table;
    protected int size;
    protected int collisions;
    protected int[] bucketCollisions;

    public HashTable(int size) {
        this.size = size;
        this.collisions = 0;
        this.table = new ArrayList<>(size);
        this.bucketCollisions = new int[size]; // Array para contar colisões em cada posição
        for (int i = 0; i < size; i++) {
            table.add(new LinkedList<>());
        }
    }

    protected abstract int hashFunction(String key);

    public void insert(String key) {
        int index = hashFunction(key);
        LinkedList<String> bucket = table.get(index);

        if (!bucket.isEmpty()) {
            collisions++;
            bucketCollisions[index]++; // Incrementa a contagem de colisões para o bucket específico
        }
        
        if (!bucket.contains(key)) {
            bucket.add(key);
        }
    }

    public boolean search(String key) {
        int index = hashFunction(key);
        return table.get(index).contains(key);
    }

    public int getCollisions() {
        return collisions;
    }

    public void printDistribution() {
        for (int i = 0; i < table.size(); i++) {
            System.out.println("Bucket " + i + ": " + table.get(i).size() + " items");
        }
    }

    public void printBucketCollisions(String functionName) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter("output_collisions.txt", true))) {
            writer.println("Bucket Collisions for " + functionName + ":");
            for (int i = 0; i < bucketCollisions.length; i++) {
                writer.println("Bucket " + i + ": " + bucketCollisions[i] + " collisions");
            }
            writer.println();
        }
    }

    public void resetTable() {
        for (int i = 0; i < size; i++) {
            table.get(i).clear();
            bucketCollisions[i] = 0; // Reinicia contagem de colisões para cada bucket
        }
        collisions = 0;
    }
}

class HashTableFunction1 extends HashTable {
    public HashTableFunction1(int size) {
        super(size);
    }

    @Override
    protected int hashFunction(String key) {
        int hash = 0;
        for (char c : key.toCharArray()) {
            hash = (hash * 31 + c) % size;
        }
        return hash;
    }
}

class HashTableFunction2 extends HashTable {
    public HashTableFunction2(int size) {
        super(size);
    }

    @Override
    protected int hashFunction(String key) {
        int hash = 5381;
        for (char c : key.toCharArray()) {
            hash = ((hash << 5) + hash) + c;
        }
        return Math.abs(hash % size);
    }
}

public class HashTest {
    public static void main(String[] args) throws IOException {
        int tableSize = 1000;
        HashTable hashTable1 = new HashTableFunction1(tableSize);
        HashTable hashTable2 = new HashTableFunction2(tableSize);

        List<String> names = loadNamesFromFile("tde3\\female_names.txt");

        long startTime, endTime;

        System.out.println("Testing Hash Function 1...");
        startTime = System.nanoTime();
        for (String name : names) {
            hashTable1.insert(name);
        }
        endTime = System.nanoTime();
        long insertTime1 = endTime - startTime;
        System.out.println("Total Collisions in Hash Function 1: " + hashTable1.getCollisions());
        System.out.println("Total Insertion Time for Hash Function 1: " + insertTime1 + " ns");

        startTime = System.nanoTime();
        for (String name : names) {
            hashTable1.search(name);
        }
        endTime = System.nanoTime();
        long searchTime1 = endTime - startTime;
        System.out.println("Total Search Time for Hash Function 1: " + searchTime1 + " ns");

        System.out.println("Distribution for Hash Function 1:");
        hashTable1.printDistribution();
        hashTable1.printBucketCollisions("Hash Function 1"); // Grava as colisões por bucket no arquivo para Hash Function 1

        System.out.println("\nTesting Hash Function 2...");
        startTime = System.nanoTime();
        for (String name : names) {
            hashTable2.insert(name);
        }
        endTime = System.nanoTime();
        long insertTime2 = endTime - startTime;
        System.out.println("Total Collisions in Hash Function 2: " + hashTable2.getCollisions());
        System.out.println("Total Insertion Time for Hash Function 2: " + insertTime2 + " ns");

        startTime = System.nanoTime();
        for (String name : names) {
            hashTable2.search(name);
        }
        endTime = System.nanoTime();
        long searchTime2 = endTime - startTime;
        System.out.println("Total Search Time for Hash Function 2: " + searchTime2 + " ns");

        System.out.println("Distribution for Hash Function 2:");
        hashTable2.printDistribution();
        hashTable2.printBucketCollisions("Hash Function 2"); // Grava as colisões por bucket no arquivo para Hash Function 2

        System.out.println("\n=== Final Report ===");
        System.out.println("Hash Function 1:");
        System.out.println("Total Collisions: " + hashTable1.getCollisions());
        System.out.println("Total Insertion Time: " + insertTime1 + " ns");
        System.out.println("Total Search Time: " + searchTime1 + " ns");

        System.out.println("\nHash Function 2:");
        System.out.println("Total Collisions: " + hashTable2.getCollisions());
        System.out.println("Total Insertion Time: " + insertTime2 + " ns");
        System.out.println("Total Search Time: " + searchTime2 + " ns");
    }

    private static List<String> loadNamesFromFile(String filename) {
        List<String> names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                names.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return names;
    }
}
