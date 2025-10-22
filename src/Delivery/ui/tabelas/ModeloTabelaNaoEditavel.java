package Delivery.ui.tabelas;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
 * Modelo de Tabela customizado onde nenhuma célula é editável.
 */
public class ModeloTabelaNaoEditavel extends DefaultTableModel {

	// Construtores para permitir a inicialização do modelo
	public ModeloTabelaNaoEditavel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	// Construtor adicional se você usa Vector (como no DefaultTableModel original)
	public ModeloTabelaNaoEditavel(Vector columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	/**
	 * Sobrescreve o método principal de editabilidade.
	 *
	 * @return Retorna sempre 'false', tornando todas as células não-editáveis.
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}