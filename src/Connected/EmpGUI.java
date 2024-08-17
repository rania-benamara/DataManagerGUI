package Connected;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;




public class EmpGUI extends JFrame {

	private JPanel contentPane;
	private JTable table;
	
	public static EmpGUI current;      /* pour sauter d'une fen�tre � l'autre */ 
	
	Connection con;
	Statement stmt;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EmpGUI.current = new EmpGUI();
					EmpGUI.current.setVisible(true);
					EditForm.current = new EditForm(); 
					EditForm.current.setVisible(false);
					
					EditProduit.current = new EditProduit(); 
					EditProduit.current.setVisible(false);
					
					EditCommande.current = new EditCommande(); 
					EditCommande.current.setVisible(false);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EmpGUI() {
		
		try {
			 String url = "jdbc:mysql://localhost:3306/ClientProduit2024";
			 con = DriverManager.getConnection(url,"root", "root");
			 stmt = con.createStatement();
			 
			 table = new JTable();
			 reload();
			
		}
		catch (Exception e) {
			 e.printStackTrace();
		}
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {					 
					stmt.close();
					con.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}				
			}
		});
		
		setResizable(false);
		setTitle("EmpGUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 445, 245);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Client");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Insert");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EditForm.current.clear();
				EditForm.current.isUpdate = false;
				EditForm.current.setTitle("Inserer client");
				EditForm.current.setVisible(true);
			}
		});
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Update");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRowCount() == 0) { 
					JOptionPane.showMessageDialog(null, "No line is selected");
				}
				else if (table.getSelectedRowCount() >1) {  
					JOptionPane.showMessageDialog(null, "Multiple lines are selected");
				}
				else {
					EditForm.current.editDataModel.setRow(getRow(table.getSelectedRows()[0]));
					EditForm.current.isUpdate = true;
					EditForm.current.setTitle("Modifier client");
					EditForm.current.setVisible(true);
				}
			}
		});
		mnNewMenu.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Delete");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRowCount() == 0) { 
					JOptionPane.showMessageDialog(null, "No line is selected");
				}
				else {
					int n_lines_to_delete = table.getSelectedRows().length;
					int opt = JOptionPane.showConfirmDialog(null, 
							   "Are you sure you want to delete "+ n_lines_to_delete+ " line(s)?",
							   "Confirmation",
							   JOptionPane.YES_NO_OPTION,
							   JOptionPane.QUESTION_MESSAGE,
							   null); 
					if (opt == 0) {
						try {
							for (int i = 0; i < n_lines_to_delete; i++) {
								stmt.executeUpdate("DELETE FROM client WHERE Cli_Id = '"
										+ table.getValueAt(table.getSelectedRows()[i], 0)+"'");
							}
							reload();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		mnNewMenu.add(mntmNewMenuItem_2);
		
		JMenuItem mntmNewMenuItem_9 = new JMenuItem("Afficher");
		mntmNewMenuItem_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reload();
			}
		});
		mnNewMenu.add(mntmNewMenuItem_9);
		
		
		/*----------------Produit-------------*/
		JMenu mnNewMenu_2 = new JMenu("Produit");
		menuBar.add(mnNewMenu_2);
		
		JMenuItem mntmNewMenuItem_4 = new JMenuItem("Insert");
		mntmNewMenuItem_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EditProduit.current.clear();
				EditProduit.current.isUpdate = false;
				EditProduit.current.setTitle("Inserer produit");
				EditProduit.current.setVisible(true);
				
			}
			
		});
		mnNewMenu_2.add(mntmNewMenuItem_4);
		
		JMenuItem mntmNewMenuItem_5 = new JMenuItem("Modifier");
		mntmNewMenuItem_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRowCount() == 0) { 
					JOptionPane.showMessageDialog(null, "No line is selected");
				}
				else if (table.getSelectedRowCount() >1) {  
					JOptionPane.showMessageDialog(null, "Multiple lines are selected");
				}
				else {
					EditProduit.current.editDataModel.setRow(getRow(table.getSelectedRows()[0]));
					EditProduit.current.isUpdate = true;
					EditProduit.current.setTitle("Modifier produit");
					EditProduit.current.setVisible(true);
				}
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_5);
		
		JMenuItem mntmNewMenuItem_6 = new JMenuItem("Supprimer");
		mntmNewMenuItem_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRowCount() == 0) { 
					JOptionPane.showMessageDialog(null, "No line is selected");
				}
				else {
					int n_lines_to_delete = table.getSelectedRows().length;
					int opt = JOptionPane.showConfirmDialog(null, 
							   "Are you sure you want to delete "+ n_lines_to_delete+ " line(s)?",
							   "Confirmation",
							   JOptionPane.YES_NO_OPTION,
							   JOptionPane.QUESTION_MESSAGE,
							   null); 
					if (opt == 0) {
						try {
							for (int i = 0; i < n_lines_to_delete; i++) {
								stmt.executeUpdate("DELETE FROM produit WHERE P_Id = '" 
										+ table.getValueAt(table.getSelectedRows()[i], 0)+"'");
							}
							reload2();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
				
			
		});
		mnNewMenu_2.add(mntmNewMenuItem_6);
		
		JMenuItem mntmNewMenuItem_10 = new JMenuItem("Afficher");
		mntmNewMenuItem_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reload2();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_10);
		
		/*------------Commande-------------*/
		JMenu mnNewMenu_1 = new JMenu("Commande");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Insert");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EditCommande.current.clear();
				EditCommande.current.isUpdate = false;
				EditCommande.current.setTitle("Inserer commande");
				EditCommande.current.setVisible(true);
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_3);
		
		JMenuItem mntmNewMenuItem_7 = new JMenuItem("Modifier");
		mntmNewMenuItem_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRowCount() == 0) { 
					JOptionPane.showMessageDialog(null, "No line is selected");
				}
				else if (table.getSelectedRowCount() >1) {  
					JOptionPane.showMessageDialog(null, "Multiple lines are selected");
				}
				else {
					EditCommande.current.editDataModel.setRow(getRow(table.getSelectedRows()[0]));
					EditCommande.current.isUpdate = true;
					EditCommande.current.setTitle("Modifier commande");
					EditCommande.current.setVisible(true);
				}
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_7);
		
		JMenuItem mntmNewMenuItem_8 = new JMenuItem("Supprimer");
		mntmNewMenuItem_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRowCount() == 0) { 
					JOptionPane.showMessageDialog(null, "No line is selected");
				}
				else {
					int n_lines_to_delete = table.getSelectedRows().length;
					int opt = JOptionPane.showConfirmDialog(null, 
							   "Are you sure you want to delete "+ n_lines_to_delete+ " line(s)?",
							   "Confirmation",
							   JOptionPane.YES_NO_OPTION,
							   JOptionPane.QUESTION_MESSAGE,
							   null); 
					if (opt == 0) {
						try {
							for (int i = 0; i < n_lines_to_delete; i++) {
								stmt.executeUpdate("DELETE FROM commande WHERE Cli_Id = '" 
										+ table.getValueAt(table.getSelectedRows()[i], 0)+"'");
							}
							reload3();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			
			
		});
		mnNewMenu_1.add(mntmNewMenuItem_8);
		
		JMenuItem mntmNewMenuItem_11 = new JMenuItem("Afficher");
		mntmNewMenuItem_11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reload3();
			}
			
		});
		mnNewMenu_1.add(mntmNewMenuItem_11);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// JScrollPane scrollPane = new JScrollPane(table);  // put table in the constructor or use setViewportView 
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);
		scrollPane.setBounds(0, 0, 428, 183);
		contentPane.add(scrollPane);		
		
	}
	
	void reload() {
		try {
			 ResultSet rs = stmt.executeQuery( "SELECT * FROM client" );

			 table.setModel(buildTableModel(rs));   
			 
		     rs.close();
		}
		catch( Exception e ) {
			 e.printStackTrace();
		}
	}
	
	void reload2() {
		try {
			 ResultSet rs = stmt.executeQuery( "SELECT * FROM produit" );

			 table.setModel(buildTableModel(rs));   
			 
		     rs.close();
		}
		catch( Exception e ) {
			 e.printStackTrace();
		}
	}
	
	void reload3() {
		try {
			 ResultSet rs = stmt.executeQuery( "SELECT * FROM commande" );

			 table.setModel(buildTableModel(rs));   
			 
		     rs.close();
		}
		catch( Exception e ) {
			 e.printStackTrace();
		}
	}
	private  Object[] getRow(int row){  	
		Object ligne[] = new Object [table.getColumnCount()]; 
		for (int i=0; i < table.getColumnCount(); i++ ) {
			ligne[i] = table.getValueAt(row, i);
		}		
//		ligne[0] = table.getValueAt(row, 0);
//		ligne[1] = table.getValueAt(row, 1); 
//		ligne[2] = table.getValueAt(row, 2);
//		ligne[3] = table.getValueAt(row, 3);
//		ligne[4] = table.getValueAt(row, 4) ;
		return ligne;			  		 
	}
	
	public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();

		// names of columns
		Vector<String> columnNames = new Vector<String>();
		int columnCount = metaData.getColumnCount();
		for (int column = 1; column <= columnCount; column++) {
			columnNames.add(metaData.getColumnName(column));
		}

		// data of the table
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		while (rs.next()) {
			Vector<Object> row = new Vector<Object>();
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				row.add(rs.getObject(columnIndex));
			}
			data.add(row);
		}

		return new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
	}
}
