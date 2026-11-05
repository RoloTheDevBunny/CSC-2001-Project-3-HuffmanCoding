import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Starter test suite for Project 3 (Huffman Encoding/Decoding).
 * Write more tests!
 * These tests assume the test files are in the same directory as HuffmanTest.java
 */
public class HuffmanTest {

    private static final String demoFilePath = "";//""src/test/java/";

    @Test
    public void testCntFreq() throws IOException {
        int[] freqList = Huffman.cntFreq(demoFilePath + "file2.txt");
        int[] ansList = {2, 4, 8, 16, 0, 2, 0}; // counts for ascii 97..103
        for (int i = 0; i < ansList.length; i++) {
            assertEquals(ansList[i], freqList[97 + i], "mismatch at ascii " + (97 + i));
        }
    }

    @Test
    public void testComesBefore() {
        HuffmanNode d = new HuffmanNode('d', 5);
        HuffmanNode k = new HuffmanNode('k', 5);
        HuffmanNode a = new HuffmanNode('a', 2);
        assertTrue(HuffmanNode.comesBefore(d, k));
        assertFalse(HuffmanNode.comesBefore(k, d));
        assertTrue(HuffmanNode.comesBefore(a, d));
        assertFalse(HuffmanNode.comesBefore(d, a));
    }

    @Test
    public void testCreateHeader() throws IOException {
        int[] freqList = Huffman.cntFreq(demoFilePath + "file2.txt");
        assertEquals("97 2 98 4 99 8 100 16 102 2", Huffman.createHeader(freqList));
        assertEquals("", Huffman.createHeader(new int[Huffman.NUM_CHARS]));
    }

    @ParameterizedTest
    @ValueSource(strings = {"file1", "file2", "multiline", "empty", "single"})
    public void testDemoFilesEncoding(String fileName) throws IOException {
        Huffman.huffmanEncode(demoFilePath + fileName + ".txt", demoFilePath + fileName + "_outEnc.txt");
        assertTrue(compareFiles(demoFilePath + fileName + "_outEnc.txt", demoFilePath + fileName + "_soln.txt"));
    }
    @ParameterizedTest
    @ValueSource(strings = {"file1", "file2", "multiline", "empty", "single"})
    public void testDemoFilesDecoding(String fileName) throws IOException {
        Huffman.huffmanDecode(demoFilePath + fileName + "_soln.txt", demoFilePath + fileName + "_outDec.txt");
        assertTrue(compareFiles(demoFilePath + fileName + "_outDec.txt", demoFilePath + fileName + ".txt"));
    }

    @Test
    public void testRemoveMinWithAnArrayList() {
        List<HuffmanNode> nodes = new ArrayList<>();
        Huffman.addNode(nodes, new HuffmanNode('z', 5));
        Huffman.addNode(nodes, new HuffmanNode('a', 2));
        Huffman.addNode(nodes, new HuffmanNode('b', 3));

        HuffmanNode minimum = Huffman.removeMin(nodes);

        assertEquals('a', minimum.charAscii());
        assertEquals(2, minimum.freq());
        assertEquals(2, nodes.size());
        assertFalse(nodes.contains(minimum));
        assertNull(Huffman.removeMin(new ArrayList<>()));
    }

    @Test
    public void testCreateCodeWithNoTree() {
        String[] codes = Huffman.createCode(null);

        assertEquals(Huffman.NUM_CHARS, codes.length);
        assertEquals("", codes['a']);
    }

    @Test
    public void testCombine() {
        HuffmanNode larger = new HuffmanNode('z', 5);
        HuffmanNode smaller = new HuffmanNode('a', 2);

        HuffmanNode parent = HuffmanNode.combine(larger, smaller);

        assertEquals(smaller, parent.left());
        assertEquals(larger, parent.right());
        assertEquals(7, parent.freq());
        assertEquals(0, smaller.compareTo(smaller));
    }

    @Test
    public void testCreateHuffTree() {
        int[] emptyFrequencies = new int[Huffman.NUM_CHARS];
        assertNull(Huffman.createHuffTree(emptyFrequencies));

        int[] singleFrequency = new int[Huffman.NUM_CHARS];
        singleFrequency['x'] = 4;
        HuffmanNode singleLeaf = Huffman.createHuffTree(singleFrequency);
        assertTrue(singleLeaf.isLeaf());
        assertEquals('x', singleLeaf.charAscii());
        assertEquals("", Huffman.createCode(singleLeaf)['x']);

        int[] twoFrequencies = new int[Huffman.NUM_CHARS];
        twoFrequencies['a'] = 1;
        twoFrequencies['b'] = 2;
        String[] codes = Huffman.createCode(Huffman.createHuffTree(twoFrequencies));
        assertEquals("0", codes['a']);
        assertEquals("1", codes['b']);
    }

    @Test
    public void testParseHeader() {
        int[] frequencies = Huffman.parseHeader("32 3 97 4 122 2");
        assertEquals(3, frequencies[32]);
        assertEquals(4, frequencies['a']);
        assertEquals(2, frequencies['z']);
        assertArrayEquals(new int[Huffman.NUM_CHARS], Huffman.parseHeader(""));
    }

    @Test
    public void testDecodeMissingFile() {
        assertThrows(FileNotFoundException.class, () -> Huffman.huffmanDecode("missing-huffman-input.txt", "unused-output.txt"));
    }

    /**
     * Compares two files line by line, trimming trailing CR/LF so the
     * comparison works the same on Windows and Unix-style line endings.
     */
    private static boolean compareFiles(String file1, String file2) throws IOException {
        try (BufferedReader r1 = new BufferedReader(new FileReader(file1));
             BufferedReader r2 = new BufferedReader(new FileReader(file2))) {
            String line1, line2;
            while (true) {
                line1 = r1.readLine();
                line2 = r2.readLine();
                if (line1 == null && line2 == null) {
                    return true;
                }
                if (line1 == null || line2 == null) {
                    return false;
                }
                if (!line1.trim().equals(line2.trim())) {
                    return false;
                }
            }
        }
    }
}
