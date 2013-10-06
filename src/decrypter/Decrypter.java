/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package decrypter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jasypt.util.text.BasicTextEncryptor;

/**
 *
 * @author research
 */
public class Decrypter {

    /**
     * @param args the command line arguments
     */
    private BasicTextEncryptor encryptor;

    public Decrypter() {
        encryptor = new BasicTextEncryptor();
    }

    public void setPassword(String password) {
        encryptor.setPassword(password);
    }

    private String readFile( String file ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }

        return stringBuilder.toString();
    }
    private void writeFile(String filePath, String content) {
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(filePath);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(f,
                    "utf8");
            myOutWriter.append(content);
            myOutWriter.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void decrypt(String delimitedPaths, String outpath) {
        String[] paths = delimitedPaths.split(";");
        ArrayList<File> next = new ArrayList<File>();
        Stack<File> stack = new Stack<File>();
        File f;
        for (String s : paths) {
            f = new File(s);
            if (f.isDirectory()) {
                // does not care about the parent folder.
                for (File x : f.listFiles()) {
                    stack.push(x);
                }
            } else {
                stack.push(f);
            }
        }
        File popped;
        while (!stack.empty()) {
            popped = stack.pop();
            String content;
            try {
                content = readFile(popped.getAbsolutePath());
            } catch (IOException ex) {
                content = "";
                Logger.getLogger(Decrypter.class.getName()).log(Level.SEVERE, null, ex);
            }
            String result = encryptor.decrypt(content);
            writeFile(outpath + "/" + popped.getName(), result);
        }
    }

    public static void main(String[] args) {
        
    }
}
