import java.io.*;
import java.util.*;

/**
 * Project 3: Huffman Encoding / Decoding.
 *
 * Implement the methods below to compress and decompress text files using
 * Huffman coding. See the assignment write-up for the full specification.
 *
 *  - Treat characters as 8-bit values in the range [0, 255].
 *  - When writing output files, write the newline character explicitly as
 *    '\n' (do not rely on System.lineSeparator() / println()), so your
 *    output is identical across Windows/Mac/Linux. A BufferedWriter over
 *    a FileWriter, writing "\n" yourself, works well for this.
 *  - Encoded files store the code as literal '0'/'1' characters (one byte
 *    each), not as packed bits, so the "encoded" file will actually be
 *    larger than the original -- that's expected for this assignment.
 *  - createHuffTree() is built on top of addNode()/removeMin(), which
 *    operate generically over a java.util.Collection<HuffmanNode>.
 *  - HuffmanNode is a record, so its components are read with
 *    node.charAscii(), node.freq(), node.left(), and node.right() -- no
 *    "get" prefix.
 */
public class Huffman {

    /** Number of possible 8-bit character values. */
    public static final int NUM_CHARS = 256;

    /**
     * Opens the text file named filename and counts the frequency of
     * occurrence of every character in it.
     * Returns an int array of size NUM_CHARS, indexed by ASCII value, with
     * the count of each character (0 for characters that do not occur).
     */
    public static int[] cntFreq(String filename) throws IOException {
        int[] freq = new int[NUM_CHARS];
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            while (line != null) {
                for (char c :  line.toCharArray()) {
                    freq[c]++;
                }
                line = reader.readLine();
            }
        }
        return freq;
    }

    /**
     * Adds node to collection, preserving whatever ordering your chosen
     * implementation of collection relies on to make the minimum element
     * easy to find.
     *
     * You choose what kind of Collection to pass in when you call this
     * from createHuffTree(). For example, an ArrayList kept sorted by
     * HuffmanNode.comesBefore() as you insert (an "ordered list"), or a
     * PriorityQueue. How you implement this method depends on which you pick.
     */
    public static void addNode(Collection<HuffmanNode> collection, HuffmanNode node) {
        // TODO: implement
        collection.add(node);
    }

    /**
     * Removes and returns the smallest HuffmanNode in collection, using
     * HuffmanNode.comesBefore() (or compareTo(), which relies on it) to
     * determine "smallest". Returns null if collection is empty.
     *
     * This must work for ANY Collection passed in -- not just the one you
     * use inside createHuffTree(). For example, if you back createHuffTree()
     * with a PriorityQueue, poll() it; for any other collection, find the
     * minimum node and remove it.
     */
    public static HuffmanNode removeMin(Collection<HuffmanNode> collection) {
        if (collection instanceof PriorityQueue<HuffmanNode> pq) {
            return pq.poll();
        }

        HuffmanNode min = null;
        for (HuffmanNode node : collection) {
            if (min == null || node.compareTo(min) < 0) {
                min = node;
            }
        }
        if (min != null) {
            collection.remove(min);
        }
        return min;
    }

    /**
     * Builds a Huffman tree from an array of character frequencies (as
     * produced by cntFreq()) and returns the root HuffmanNode of the tree.
     *
     * Use addNode()/removeMin() above to manage the collection of
     * not-yet-combined nodes as you build the tree -- pick whichever
     * concrete Collection type you want to back them with (e.g. a sorted
     * ArrayList, or a PriorityQueue). The rest of this method, and the
     * resulting tree, should not depend on which one you chose.
     *
     * Special cases:
     *  - If freq contains no non-zero counts (empty input file), return null
     *  - If freq contains exactly one non-zero count (input file has only
     *    one unique character), return a single leaf HuffmanNode for that
     *    character (no combine() call needed).
     */
    public static HuffmanNode createHuffTree(int[] freq) {
        // TODO: implement

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();

        for (int i = 0; i < 256; i++) {
            int count = freq[i];
            if (count > 0) {
                addNode(pq, new HuffmanNode(i, count));
            }
        }
        if (pq.isEmpty()) {
            return null;
        }

        if (pq.size() == 1) {
            return pq.iterator().next();
        }

        while (pq.size() >= 2) {
            HuffmanNode node1 = removeMin(pq);
            HuffmanNode node2 = removeMin(pq);

            addNode(pq, HuffmanNode.combine(node1, node2));
        }

        return removeMin(pq);
    }

    /**
     * Traverses the Huffman tree rooted at root and builds the Huffman
     * code for every character that appears in it. Going left appends
     * '0' to the code, going right appends '1'.
     *
     * Returns a String array of size NUM_CHARS, indexed by ASCII value.
     * Characters that do not appear in the tree have an empty string ""
     * at their index. The same is true of the lone character in a
     * single-node tree (the only-one-unique-character case) - it has no
     * left/right branch to build a code out of; huffmanEncode/huffmanDecode
     * handle that case entirely through the header.
     */
    public static String[] createCode(HuffmanNode root) {
        String[] codes = new String[NUM_CHARS];
        Arrays.fill(codes, "");
        // TODO: implement

        if (root == null) {
            return codes;
        }
        Deque<HuffmanNode> nodeStack = new ArrayDeque<>();
        Deque<String> codeStack = new ArrayDeque<>();

        nodeStack.push(root);
        codeStack.push("");

        while (!nodeStack.isEmpty()) {
            HuffmanNode node = nodeStack.pop();
            String code = codeStack.pop();

            if (node.isLeaf()) {
                codes[node.charAscii()] = code;
            } else {
                if (node.right() != null) {
                    nodeStack.push(node.right());
                    codeStack.push(code + "1");
                }

                if (node.left() != null) {
                    nodeStack.push(node.left());
                    codeStack.push(code + "0");
                }
            }
        }

        return codes;
    }

    /**
     * Given a frequency array (as produced by cntFreq()), returns a
     * header string listing the ASCII values and their associated
     * frequencies, separated by single spaces, in ascending order of
     * ASCII value. Only characters with a non-zero count are included.
     *
     * Example: for the text "aaabbbbcc" this returns "97 3 98 4 99 2".
     */
    public static String createHeader(int[] freq) {
        // TODO: implement
        StringBuilder s = new StringBuilder();
        for (int i=0; i<NUM_CHARS; i++) {
            if (freq[i] == 0) continue;
            s.append(i).append(" ").append(freq[i]).append(" ");
        }
        if (!s.isEmpty()) {
            s.setLength(s.length() - 1);
        }

        return s.toString();
    }

    /**
     * Reads the input text file inFile and writes to outFile:
     *   1. A header line (see createHeader), followed by a newline
     *   2. The Huffman-encoded contents of inFile, using the codes from
     *      createCode() -- UNLESS inFile has only one unique character,
     *      see below.
     * If outFile already exists, its old contents are overwritten.
     *
     * Special cases:
     *  - Empty input file: outFile should end up empty too
     *  - Input file with only one unique character: should contain only the
     *    header line (e.g. "97 5") and nothing else - no second line of code bits.
     */
    public static void huffmanEncode(String inFile, String outFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
             BufferedReader reader = new BufferedReader(new FileReader(inFile))) {
            int[] freq = cntFreq(inFile);

            writer.write(createHeader(freq));

            HuffmanNode root = createHuffTree(freq);
            if (root == null || (root.right() == null && root.left() == null)) {
                return;
            }
            writer.write('\n');

            String[] key = createCode(root);

            String line = reader.readLine();
            while (line != null) {
                StringBuilder newLine = new StringBuilder();
                for (char c : line.toCharArray()) {
                    newLine.append(key[c]);
                }
                writer.write(newLine.toString());
                line = reader.readLine();
            }
        }
    }

    /**
     * Parses a header string (in the format produced by createHeader) and
     * returns the corresponding frequency array of size NUM_CHARS, indexed
     * by ASCII value, i.e. the same format returned by cntFreq().
     *
     * Example: for the header "97 3 98 4 99 2", freq[97, 98, 99] == {3, 4, 2}.
     */
    public static int[] parseHeader(String headerString) {
        int[] freq = new int[NUM_CHARS];

        String[] parts = headerString.trim().split("\\s+");

        for (int i=0; i<parts.length; i+=2) {
            freq[Integer.parseInt(parts[i])] = Integer.parseInt(parts[i+1]);
        }

        return freq;
    }

    /**
     * Reads an encoded file (in the format written by huffmanEncode) and
     * writes the decoded text to decodeFile.
     *
     * Recreate the frequency array from the header with parseHeader(),
     * rebuild the Huffman tree with createHuffTree(), then walk the tree
     * for each bit of the second line: '0' -> left child, '1' -> right
     * child. When a leaf is reached, emit its character and restart at
     * the root.
     *
     * Special case: if the recreated tree is a single leaf (the
     * only-one-unique-character case), there is no second line to read
     * The header's count alone tells you how many times that character occurred,
     * so write it out that many times directly.
     */
    public static void huffmanDecode(String encodedFile, String decodeFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(encodedFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(decodeFile))) {
            String line = reader.readLine();
            String[] parts = line.trim().split("\\s+");
            if (parts.length < 2) {
                writer.write("");
                return;
            } else if (parts.length == 2) {
                char letter = (char) Integer.parseInt(parts[0]);
                writer.write(String.valueOf(letter).repeat(Math.max(0, Integer.parseInt(parts[1]))));
            }

            int[] freq = parseHeader(line);

            HuffmanNode root = createHuffTree(freq);

            line = reader.readLine();
            while (line != null) {
                StringBuilder newLine = new StringBuilder();

                HuffmanNode node = root;

                for (char bit : line.toCharArray()) {
                    if (bit == '0') {
                        node = node.left();
                    } else if (bit == '1') {
                        node = node.right();
                    }

                    if (node.isLeaf()) {
                        writer.write(node.charAscii());
                        node = root;
                    }
                }

                writer.write(newLine.toString());
                line = reader.readLine();
            }
        }
    }
}
