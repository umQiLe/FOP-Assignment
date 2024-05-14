package assignmentfop;

import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

//BarChart Constructor
public class BarChart_AWT extends JFrame {

    public BarChart_AWT( String applicationTitle , String chartTitle, String [] X_DATA, int [] Y_DATA ) {
        super( applicationTitle );
        JFreeChart barChart = ChartFactory.createBarChart(
                chartTitle,
                "Category",
                "Score",
                createDataset(X_DATA, Y_DATA),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel( barChart );
        chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );
        setContentPane( chartPanel );
    }

    private CategoryDataset createDataset(String [] X_DATA, int [] Y_DATA) {

        String users = "User";
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
        for (int i = 0; i < X_DATA.length; i++) {
            dataset.addValue(Y_DATA[i], X_DATA[i], users);
        }

        return dataset;
    }

}
