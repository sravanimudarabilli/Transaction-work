package bank;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.Color;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;
import javax.swing.JTable;
import javax.swing.JLabel;

import java.util.Random;
import net.proteanit.sql.DbUtils;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

public class Frame {

	private JFrame frame;
	private int balance;
	private JLabel lblBalance;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame window = new Frame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	Connection con = null;
	private JTable table;
	private JLabel lblLastFiveTransactions;
	private JLabel lblTrnid;
	private JLabel lblType;
	private JLabel lblAmount;
	private JLabel lblBalance_1;
	/**
	 * Create the application.
	 */
	public Frame() {
		con = OracleCon.connectToDB();
		try {
			PreparedStatement pst = con.prepareStatement("Select cur_balance from transactions order by trn_id", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = pst.executeQuery();
			rs.last();
			balance = rs.getInt("cur_balance");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
		}
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("serial")
	private void initialize() {
		Random rand = new Random();
		frame = new JFrame();
		frame.setResizable(false);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 500, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton fndTrnBtn = new JButton("Transfer fund");
		fndTrnBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (balance < 1000) {
					JOptionPane.showMessageDialog(null,  "Not enough balance, please add funds and try again");
				} else {
					balance -= 1000;
					try {
						PreparedStatement pst = con.prepareStatement("INSERT INTO Transactions values(trns.nextval, \'dr.\', " + 1000 + ", " + balance + ")");
						pst.execute();
						pst = con.prepareStatement("select * from Transactions order by trn_id desc");
						ResultSet rs = pst.executeQuery();
						lblLastFiveTransactions.setVisible(true);
						lblTrnid.setVisible(true);
						lblType.setVisible(true);
						lblAmount.setVisible(true);
						lblBalance_1.setVisible(true);
						table.setModel(DbUtils.resultSetToTableModel(rs));
						rs.close();
						pst.close();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1);
					}
					lblBalance.setText("" + balance);
				}
			}
		});
		fndTrnBtn.setBackground(Color.WHITE);
		fndTrnBtn.setForeground(Color.GREEN);
		fndTrnBtn.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		fndTrnBtn.setBounds(324, 316, 150, 34);
		frame.getContentPane().add(fndTrnBtn);
		
		JButton addMn = new JButton("Add funds");
		addMn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int amt = 1000 + rand.nextInt(500);
				balance += amt;
				try {
					PreparedStatement pst = con.prepareStatement("INSERT INTO Transactions values(trns.nextval, \'cr.\', " + amt + ", " + balance + ")");
					pst.execute();
					pst.close();
					pst = con.prepareStatement("select * from Transactions order by trn_id desc");
					ResultSet rs = pst.executeQuery();
					lblLastFiveTransactions.setVisible(true);
					lblTrnid.setVisible(true);
					lblType.setVisible(true);
					lblAmount.setVisible(true);
					lblBalance_1.setVisible(true);
					table.setModel(DbUtils.resultSetToTableModel(rs));
					rs.close();
					pst.close();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1);
				}
				lblBalance.setText("" + balance);
				
			}
		});
		addMn.setForeground(Color.ORANGE);
		addMn.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		addMn.setBackground(Color.WHITE);
		addMn.setBounds(164, 316, 150, 34);
		frame.getContentPane().add(addMn);
		
		JLabel lblNewLabel = new JLabel("Current Balance: \u20B9");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setBounds(208, 11, 122, 26);
		frame.getContentPane().add(lblNewLabel);
		
		lblBalance = new JLabel("" + balance);
		lblBalance.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblBalance.setForeground(new Color(0, 128, 0));
		lblBalance.setBounds(333, 11, 103, 26);
		frame.getContentPane().add(lblBalance);
		
		table = new JTable(){
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false; //Disallow the editing of any cell
				}
				};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setFillsViewportHeight(true);
		table.setRowHeight(30);
		table.setShowVerticalLines(false);
		table.setFont(new Font("Tahoma", Font.PLAIN, 15));
		table.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		table.setBounds(10, 123, 464, 158);
		frame.getContentPane().add(table);
		
		lblLastFiveTransactions = new JLabel("Last five transactions");
		lblLastFiveTransactions.setVisible(false);
		lblLastFiveTransactions.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblLastFiveTransactions.setBounds(150, 48, 166, 26);
		frame.getContentPane().add(lblLastFiveTransactions);
		
		lblTrnid = new JLabel("Trn_id");
		lblTrnid.setVisible(false);
		lblTrnid.setBounds(23, 98, 48, 14);
		frame.getContentPane().add(lblTrnid);
		
		lblType = new JLabel("Type");
		lblType.setVisible(false);
		lblType.setBounds(137, 98, 48, 14);
		frame.getContentPane().add(lblType);
		
		lblAmount = new JLabel("Amount");
		lblAmount.setVisible(false);
		lblAmount.setBounds(282, 98, 48, 14);
		frame.getContentPane().add(lblAmount);
		
		lblBalance_1 = new JLabel("Balance");
		lblBalance_1.setVisible(false);
		lblBalance_1.setBounds(417, 98, 48, 14);
		frame.getContentPane().add(lblBalance_1);
	}
}
