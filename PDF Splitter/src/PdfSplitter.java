import java.awt.*;
import java.io.*;
import javax.swing.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfSplitter extends JFrame {
    private JPanel panel;
    private JButton selectBtn, splitBtn;
    private JLabel fileLabel, rangeLabel;
    private JTextField rangeField;
    private File selectedFile;

    public PdfSplitter() {
        panel = new JPanel();
        selectBtn = new JButton("Select PDF");
        splitBtn = new JButton("Split PDF");
        fileLabel = new JLabel("No file selected");
        rangeLabel = new JLabel("Enter page range: (Eg: 1-5)");
        rangeField = new JTextField(10);

        selectBtn.addActionListener(e -> selectFile());
        splitBtn.addActionListener(e -> splitPdf());
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(selectBtn);
        panel.add(fileLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(rangeLabel);
        panel.add(rangeField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(splitBtn);

        add(panel);
        setSize(400, 170);
        setTitle("Split PDF");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            fileLabel.setText(selectedFile.getName());
        }
    }

   private void splitPdf() {
    if (selectedFile == null) {
        JOptionPane.showMessageDialog(this, "Please select a PDF file!");
        return;
    }
    String[] range = rangeField.getText().split("-");
    if (range.length != 2) {
        JOptionPane.showMessageDialog(this, "Invalid page range! Please enter a range in the format 'start-end'.");
        return;
    }
    int startPage = Integer.parseInt(range[0]);
    int endPage = Integer.parseInt(range[1]);
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Split PDFs");
    fileChooser.setCurrentDirectory(selectedFile);
    int result = fileChooser.showSaveDialog(this);
    
    if (result == JFileChooser.APPROVE_OPTION) {
        try {
            PDDocument document = Loader.loadPDF(selectedFile);
            Splitter splitter = new Splitter();
            splitter.setStartPage(startPage);
            splitter.setEndPage(endPage);
            splitter.setSplitAtPage(startPage - 1);
            splitter.setSplitAtPage(endPage - 1);
            
            for (PDDocument page : splitter.split(document)) {
                String fileName = fileChooser.getSelectedFile().getName().replace(".pdf", "") + ".pdf";
                File saveFile = new File(fileChooser.getSelectedFile().getParentFile(), fileName);
                page.save(saveFile);
                page.close();
            }
            document.close();
            JOptionPane.showMessageDialog(this, "PDF file has been split and saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while splitting the PDF file!");
        }
     }
   }
   
   public static void main(String[] args) {
        PdfSplitter pdfsplit = new PdfSplitter();
    }
}