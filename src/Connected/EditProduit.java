package Connected;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import Connected.EditForm.MyEditTableModel;

public class EditProduit extends JDialog {

	private final JPanel contentPane;
	private JTable table;
	
	boolean isUpdate = true; 
	
	public static EditProduit current;    /* pour sauter d'une fen�tre � l'autre */
	
	
	class MyEditTableModel extends DefaultTableModel {
		private String columnNames []={"P_Id", "P_Desc", "P_Prix"};
		
		public MyEditTableModel(){
			super();
			/* this.setColumnCount(columnNames.length); */
			this.setColumnIdentifiers(columnNames);
			this.setRowCount(1); 
		}
		
		public void setRow(Object lineMain[]){  
			this.setRowCount(0);  				
			this.addRow(lineMain);			    		 
		    }
		
		public Object[] getRow(){  	
			Object line[] = new Object [columnNames.length];
			line[0]=this.getValueAt(0, 0);
			line[1]=this.getValueAt(0, 1);
			line[2]=this.getValueAt(0, 2);
			
			return line;			  		 
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
		    if (isUpdate && column==0) { return false;} 
			else return true;
		}
	}
	
	MyEditTableModel editDataModel = new MyEditTableModel(); 
	
	public void clear(){ 
		//editDataModel.setRow(new Object[] {null, null, null, null , null});
		editDataModel.setRowCount(0);  // Delete the lines (there is only one)
		editDataModel.setRowCount(1);  // Add an empty line
	}

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					EditForm frame = new EditForm();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public EditProduit() {
		setResizable(false);
		setBounds(100, 100, 730, 183);
		
		setModal(true);      // pour �tre un "Dialog"
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);   // pour �tre un "Dialog"
	    
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		table = new JTable(editDataModel);	
		//ou
		// table = new JTable();
		// table.setModel(editDataModel);
		table.setRowHeight(30); 
		/* 
		 * pour garantir que l'edition d'une cellule termine si le focus sort de la table 
		 */
		table.putClientProperty("terminateEditOnFocusLost", true); 
		
		JScrollPane scrollPane = new JScrollPane(table);
		// ou 
		// JScrollPane scrollPane = new JScrollPane();
		// scrollPane.setViewportView(table);
		scrollPane.setBounds(0, 0, 717, 53);
		contentPane.add(scrollPane);
		
		JButton btnNewButton = new JButton("OK");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isUpdate) {                    
		            if (myValidate()) {
		                try {
		                    PreparedStatement stmt = EmpGUI.current.con.prepareStatement(
		                            "UPDATE produit SET P_Desc = ?, P_Prix = ? WHERE P_Id = ?"
		                    );
		                    stmt.setString(1, (String) table.getValueAt(0, 1));
		                    stmt.setString(2, (String) table.getValueAt(0, 2));
		                    stmt.setString(3, (String) table.getValueAt(0, 0));
		                    stmt.execute();
		                    stmt.close();
		                    EmpGUI.current.reload2();
		                    JOptionPane.showMessageDialog(null, "Base de données modifiée");
		                } catch (SQLException e1) {
		                    JOptionPane.showMessageDialog(null, "Erreur lors de la modification des données : " + e1.getMessage());
		                    e1.printStackTrace(); 
		                }                        
		                EditProduit.current.dispose();  
		            } 
		        } else { // insertion
		            if (myValidate()) {
		                try {
		                    PreparedStatement stmt = EmpGUI.current.con.prepareStatement(
		                            "INSERT INTO produit (P_Id, P_Desc, P_Prix) VALUES (?, ?, ?)"
		                    );
		                    stmt.setString(1, (String)table.getValueAt(0, 0));
		                    stmt.setString(2, (String)table.getValueAt(0, 1));
		                    stmt.setString(3, (String)table.getValueAt(0, 2));
		                    stmt.execute();
		                    stmt.close();
		                    EmpGUI.current.reload2();
		                    JOptionPane.showMessageDialog(null, "Données insérées dans la base de données");
		                } catch (SQLException e1) {
		                    JOptionPane.showMessageDialog(null, "Erreur lors de l'insertion des données : " + e1.getMessage());
		                    //e1.printStackTrace(); 
		                }                        
		                EditProduit.current.dispose();
		            } 
		        } 
		    }

		});
		
		btnNewButton.setBounds(476, 100, 89, 23);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Cancel");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EditProduit.current.dispose();   // pour fermer la fen�tre. 
			}
		});
		btnNewButton_1.setBounds(594, 100, 89, 23);
		contentPane.add(btnNewButton_1);
	}
	
	private boolean myValidate() {	
		/* 
		 *  Si c'est une insertion, nous devons nous assur� que l'id n'existe pas d�j� 
		 *  dans la base de donn�es, parce que l'id est cl�. 
		 * 
		 */ 
		if(!isUpdate) {      // si c'est une insertion
			if(!isValidateID(table.getValueAt(0, 0))) {    // si le nouveau id est invalide (n�gatif)
				JOptionPane.showMessageDialog(null, "Id invalide");
				return false;				                  // dans ce cas, la fonction fini ici 
			}
			/*
			 * Si le nouveu id est valide, if faut v�rifier s'il n'existe pas d�j� dans la base de donn�es
			 */
			try {
				ResultSet rs = EmpGUI.current.stmt.executeQuery( 
						"SELECT * FROM produit WHERE P_Id =  '"+ table.getValueAt(0, 0)+"'" );
				// Pour v�rifier si le ResultSet rs n'est pas vide, nous devons utiliser 
				// rs.next() Le rs.first() ne marche plus pour ResultSet.TYPE_FORWARD_ONLY.  
				if (rs.next()) {
					JOptionPane.showMessageDialog(null, "Id r�p�t�: id existe d�j� dans la base de donn�es");
					return false;                            // dans ce cas, la fonction fini ici 
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Base de donn�es inaccessible");
				return false;                                // dans ce cas, la fonction fini ici 
			}		
		}  // end if(!isUpdate)
		/*
		 * Alors, � ce point:
		 * 1. Si c'est une insertion, l'id est valide et il n'existe pas dans la base de donn�es.
		 * 2. Si c'est un mis-�-jour, alors il n'a rien a v�rifier, parce que l'id ne peut pas �tre chang�.	
		 * 
		 * Tout ce qui reste � faire est la validation des autres champs.
		 * 	  
		 */
		/* 
		 * Validation de l'�ge.
		 */
		
		/* 
		 * Validation du salaire
		 */
		
		return true;
	}
	
	static boolean isNonNegInteger(Object s) {
		if (s==null){return false; }
		try{
			 int r=Integer.parseInt(""+s);  
			 if (r>=0) { return true; }
			 else { return false; }
		}
		catch (Exception e){
			return false;
		}
	}
	
	static boolean isNonNegDouble(Object s) {
		if (s==null){return false; }
		try{
			 double r=Double.parseDouble(""+s);
			 if (r>=0) { return true; }
			 else { return false; }
		}
		catch (Exception e){
			return false;
		}
	}
	
	 static boolean isValidateID(Object object) {
		
        if (((String) object).length() != 4) {
            return false;
        }
        
        if (((String) object).charAt(0) != 'P') {
            return false;
        }

        for (int i = 1; i < ((String) object).length(); i++) {
            if (!isNonNegInteger(((String) object).charAt(i))) {
                return false;
            }
        }

        return true;
	}
	

}
