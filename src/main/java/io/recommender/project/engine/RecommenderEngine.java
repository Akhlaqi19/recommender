package io.recommender.project.engine;

import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.ReloadFromJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

@Service
public class RecommenderEngine {

	@Autowired
	private JDBCDataSource jdbc;

	public List<RecommendedItem> getRecommendation(int user_id, int number_of_item) throws TasteException {

		MysqlDataSource dataSource = jdbc.getDataSource();
		JDBCDataModel dataModel = new MySQLJDBCDataModel(dataSource, "user_rating", "user_id", "item_id", "rating",
				"timestamp");
		ReloadFromJDBCDataModel datamodel2 = new ReloadFromJDBCDataModel(dataModel);
		
		UserSimilarity sim = new PearsonCorrelationSimilarity(datamodel2);
		
		UserNeighborhood nbh = new NearestNUserNeighborhood(40, sim, datamodel2);
		
		Recommender recsys = new GenericUserBasedRecommender(datamodel2, nbh, sim);
		recsys.refresh(null);
		List<RecommendedItem> recommendations = recsys.recommend(user_id, number_of_item);
		
		System.out.println("____________________________");
		for(RecommendedItem item:recommendations){
			System.out.println(item.getItemID()+","+item.getValue());
		}
		return recommendations;

	}
}
