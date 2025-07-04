import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class ToDoListApp {

    private JFrame frame;
    private JTextField taskField;
    private JPanel taskPanel;
    private ArrayList<JPanel> taskItems;
    private File taskFile = new File("tasks.txt");

    public ToDoListApp() {
        frame = new JFrame("📝 To-Do List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        taskItems = new ArrayList<>();

        // Top panel with input and add button
        JPanel topPanel = new JPanel(new BorderLayout());
        taskField = new JTextField();
        JButton addButton = new JButton("Add Task");

        topPanel.add(taskField, BorderLayout.CENTER);
        topPanel.add(addButton, BorderLayout.EAST);

        // Task panel inside scroll pane
        taskPanel = new JPanel();
        taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(taskPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Load past tasks
        loadTasksFromFile();

        // Add action for "Add Task"
        addButton.addActionListener(e -> addTask(taskField.getText().trim(), true));

        // Add panels to frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    // Add a task to the list and optionally save to file
    private void addTask(String taskText, boolean saveToFile) {
        if (taskText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Task cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel taskItem = new JPanel(new BorderLayout());
        taskItem.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel taskLabel = new JLabel(taskText);
        JButton deleteButton = new JButton("Delete");

        deleteButton.addActionListener(e -> {
            taskPanel.remove(taskItem);
            taskItems.remove(taskItem);
            taskPanel.revalidate();
            taskPanel.repaint();
            deleteTaskFromFile(taskText);
        });

        taskItem.add(taskLabel, BorderLayout.CENTER);
        taskItem.add(deleteButton, BorderLayout.EAST);

        taskPanel.add(taskItem);
        taskItems.add(taskItem);

        taskPanel.revalidate();
        taskPanel.repaint();

        taskField.setText("");

        if (saveToFile) {
            saveTaskToFile(taskText);
        }
    }

    // Load past tasks from file
    private void loadTasksFromFile() {
        if (!taskFile.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(taskFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                addTask(line, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save a new task to file
    private void saveTaskToFile(String task) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(taskFile, true))) {
            bw.write(task);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Delete task from file
    private void deleteTaskFromFile(String taskToDelete) {
        try {
            File tempFile = new File("tasks_temp.txt");
            try (BufferedReader reader = new BufferedReader(new FileReader(taskFile));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.equals(taskToDelete)) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }

            // Replace old file
            if (taskFile.delete()) {
                tempFile.renameTo(taskFile);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListApp::new);
    }
}
