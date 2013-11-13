import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.awt.*;
public class client extends JFrame implements ActionListener, KeyListener {
	static int SERVERPORT = 8088;
	static String SERVERADDRESS = "127.0.0.1";
	private File file;
	private BufferedReader cin = null;
	private PrintWriter cout = null;
	private Socket client = null;
	public String clientname;
	private JPanel jPanel1 = new JPanel();
	private JTextField jTextSendInfo = new JTextField(38);
	private JTextArea jTextGetInfo = new JTextArea(16, 33);
	public JScrollPane areaScrollPane = new JScrollPane(jTextGetInfo);
	private Button Send = new Button("Send");
	private Button Link = new Button("Log in");
	private Button Sendfile = new Button("SendFile");
	private Button Receive = new Button("ReceiveFile");

	public client() {
		super("Chat Room");
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setSize(480, 465);
		jPanel1.add("South ", jTextSendInfo);
		jPanel1.setBackground(new Color(7, 149, 255));
		jTextGetInfo.setBackground(new Color(255, 255, 255));
		jTextGetInfo.setFont(new java.awt.Font("Dialog ", 0, 15));
		jTextGetInfo.setForeground(new Color(0, 0, 0)); // font color
		this.setResizable(false);
		Send.setBackground(Color.lightGray);
		Send.setForeground(Color.black);
		Send.setBounds(new Rectangle(92, 400, 90, 37));
		Link.setBackground(Color.lightGray);
		Link.setForeground(Color.black);
		areaScrollPane
			.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setAutoscrolls(true);
		jTextSendInfo.setText(" ");
		jPanel1.add("North ", areaScrollPane);
		jTextGetInfo.setEditable(false);
		jPanel1.add("South ", Sendfile);
		Sendfile.setEnabled(false);
		jPanel1.add("South ", Receive);
		Receive.setEnabled(false);
		jPanel1.add("South ", Send);
		Send.setEnabled(false);

		Sendfile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
				if (chooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
				}
				try {
				DatagramSocket ds = new DatagramSocket(8088,
					InetAddress.getByName("0.0.0.0"));
				InputStream in = new FileInputStream(file);
				byte[] b = new byte[10240];
				int c = 0;
				c = in.read(b);
				in.close();
				DatagramPacket dp = new DatagramPacket(b, b.length);
				System.out.println("fuck"+":"+dp.toString());
				ds.send(dp);
				} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, ex.toString(),
					"Exception Detected", JOptionPane.WARNING_MESSAGE);
				}
				}
				});
		Receive.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
				if (chooser.showSaveDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
				}

				try {
				/*ServerSocket server = new ServerSocket(6000);
				  Socket socket = server.accept();
				  System.out.println("Here");
				  InputStream in = socket.getInputStream();
				  byte[] buf = new byte[1024];
				  int len = 0;
				  javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
				  if (chooser.showSaveDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
				  file = chooser.getSelectedFile();
				  }
				  OutputStream writer1 = new FileOutputStream(file);
				  while ((len = in.read(buf)) > 0) {
				  writer1.write(buf, 0, len);
				  writer1.flush();
				  }
				  writer1.close();*/


					byte[] b = new byte[10240];
					DatagramSocket ds2 = new DatagramSocket(6000);
					DatagramPacket dp2 = new DatagramPacket(b,b.length);
					ds2.receive(dp2);
					OutputStream out=new FileOutputStream(file);
					out.write(dp2.getData());
					out.close();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.toString(),
							"Exception Detected", JOptionPane.WARNING_MESSAGE);
				}
				}
		});
		jPanel1.add("South ", Link);
		Send.addActionListener(this);
		jTextSendInfo.addKeyListener(this);
		Link.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				if (!jTextSendInfo.getText().equals(" ")) {
				clientname = jTextSendInfo.getText();
				connect();
				Link.setEnabled(false);
				Send.setEnabled(true);
				Sendfile.setEnabled(true);
				Receive.setEnabled(true);
				jTextSendInfo.setText(" ");
				} else
				JOptionPane.showMessageDialog(null, "Warning: Empty User Name!!",
					"Exception Detected ", JOptionPane.WARNING_MESSAGE);
				}
				});

		this.setContentPane(jPanel1);
	}

	public void connect() {
		try {
			client = new Socket(SERVERADDRESS, SERVERPORT);
			cout = new PrintWriter(client.getOutputStream());
			cin = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
			cout.println("Welcome[" + clientname + "] to chat room");
			cout.flush();
			Receiver r = new Receiver();
			r.start();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			quit();
		}
		super.processWindowEvent(e);
	}

	public static void main(String[] args) {
		client cp = new client();
		cp.show();

	}

	class Receiver extends Thread {
		public void run() {
			String msg = null;
			JScrollBar sb;
			try {
				msg = cin.readLine();
				while (true) {
					jTextGetInfo.append(msg + "\n ");
					sb = areaScrollPane.getVerticalScrollBar();
					sb.setValue(sb.getMaximum() + 50);
					jTextSendInfo.setEnabled(true);
					msg = cin.readLine();
				}
			} catch (Exception e) {
				Send.setEnabled(false);
			}
		}
	}

	void quit() {
		try {
			cout.println("[" + clientname + "]byebye ");
			cout.flush();
			cout.println("exit ");
			cout.flush();
			System.out.print("lianjieduankai ");
			cin.close();
			cout.close();
			client.close();
		} catch (Exception e) {
		} finally {
			System.exit(0);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (!jTextSendInfo.getText().equals(" ")) {
			cout.println("[" + clientname + "]" + jTextSendInfo.getText());
			cout.flush();
			jTextSendInfo.setText(" ");
			jTextSendInfo.setFocusable(true);
		} else
			JOptionPane.showMessageDialog(null, "w: empty message!!", "Exception Detected",
					JOptionPane.WARNING_MESSAGE);
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (!jTextSendInfo.getText().equals(" ")) {
				if (cout == null) {
					clientname = jTextSendInfo.getText();
					connect();
					Link.setEnabled(false);
					Send.setEnabled(true);
					jTextSendInfo.setText(" ");
				} else {
					cout.println("[ " + clientname + "]"
							+ jTextSendInfo.getText());
					cout.flush();
					jTextSendInfo.setText(" ");
					jTextSendInfo.setFocusable(true);
				}
			} else {
				if (cout == null) {
					JOptionPane.showMessageDialog(null, "Warning: Empty User Name!!",
							"Exception Detected ", JOptionPane.WARNING_MESSAGE);
				} else
					JOptionPane.showMessageDialog(null, "Warning: Empty Message!!",
							"Exception Detected ", JOptionPane.WARNING_MESSAGE);
			}

		}
	}

}