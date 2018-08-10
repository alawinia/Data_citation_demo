package edu.upenn.cis.citation.final_test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.json.JSONException;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Corecover.Subgoal;
import edu.upenn.cis.citation.Pre_processing.Query_operation;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;
import edu.upenn.cis.citation.aggregation.Aggregation5;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.Head_strs2;
import edu.upenn.cis.citation.citation_view.Covering_set;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.examples.Example_real;
import edu.upenn.cis.citation.examples.Load_views_and_citation_queries;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_full_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2_test;
import edu.upenn.cis.citation.reasoning2.Tuple_level_approach_min;
import edu.upenn.cis.citation.reasoning2.Tuple_reasoning1_min_test;
import edu.upenn.cis.citation.reasoning2.Semi_schema_level_approach_min;
import edu.upenn.cis.citation.reasoning2.Tuple_reasoning2_min_test;
import edu.upenn.cis.citation.stress_test.query_generator;
import edu.upenn.cis.citation.stress_test.view_generator;
import edu.upenn.cis.citation.user_query.query_storage;

public class final_real_test_min {
	
	static int size_range = 100;
	
	static int times = 1;
	
	static int size_upper_bound = 5;
	
	static int num_views = 3;
	
	static int view_max_size = 40;
	
	static int upper_bound = 20;
	
	static int query_num = 8;
	
	static boolean store_covering_set = true;
	
	static Vector<String> relations = new Vector<String>();
	
	static String path = "real_example/";
	
	static String path2 = "reasoning_results/";
	
	static Vector<String> get_unique_relation_names(Query query)
	{
		Vector<String> relation_names = new Vector<String>();
		
		HashSet<String> relations = new HashSet<String>();
		
		for(int i = 0; i<query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			relations.add(query.subgoal_name_mapping.get(subgoal.name));
			
		}
		
		relation_names.addAll(relations);
		
		return relation_names;
		
	}
	
	static Vector<String> get_relation_names(Query query)
	{
		Vector<String> relation_names = new Vector<String>();
				
		for(int i = 0; i<query.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			relation_names.add(query.subgoal_name_mapping.get(subgoal.name));
			
		}
				
		return relation_names;
		
	}
	
	public static void get_table_size(Vector<String> relations, Connection c, PreparedStatement pst) throws SQLException
	{
		int original_size = 0;
		
		int annotated_relation_size = 0;
		
		for(int i = 0; i<relations.size(); i++)
		{
			original_size += get_single_table_size(relations.get(i), c, pst);
			
//			annotated_relation_size += get_single_table_size(relations.get(i) + populate_db.suffix, c, pst);
		}
		
		System.out.println("original_relation_size::" + original_size);
		
//		System.out.println("annotated_relation_size::" + annotated_relation_size);
	}
	
	static int get_single_table_size(String relation, Connection c, PreparedStatement pst) throws SQLException
	{
		String query = "SELECT pg_total_relation_size('"+ relation +"')";
		
		pst = c.prepareStatement(query);
		
		ResultSet rs = pst.executeQuery();
		
		if(rs.next())
		{
			int size =  rs.getInt(1);
			
//			int size_int = Integer.valueOf(size.substring(0, size.indexOf(" ")));
			
			return size;
		}
		
		return 0;
	}

	
	public static void main(String [] args) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
	{		
		Connection c1 = null;
		
		Connection c2 = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c1 = DriverManager
	        .getConnection(populate_db.db_url1, populate_db.usr_name , populate_db.passwd);
		
	    c2 = DriverManager
	        .getConnection(populate_db.db_url2, populate_db.usr_name , populate_db.passwd);
		
//	    System.out.println(get_single_table_size("family", c, pst));
	    
	    int view_id = Integer.valueOf(args[0]);
	    
	    boolean load_views_queries = Boolean.valueOf(args[1]);
	    
    	Vector<Query> queries = Load_views_and_citation_queries.get_views(path + "real_queries", c2, pst);
	    
	    if(load_views_queries)
	    {
		    
		    Example_real.load_view_and_citations(path + "views", path + "citation_queries", path + "connection", true, c1, pst);
		    
		    Example_real.load_view_and_citations(path + "views", path + "citation_queries", path + "connection", false, c2, pst);

	    }
	    
	    	    		
		boolean tuple_level = Boolean.valueOf(args[2]);
			
		
	    Vector<Query> views = view_operation.get_all_views(c2, pst);

		
//		for(int i = 0; i<queries.size(); i++)
		{
//			System.out.println(i);
			
			
			stress_test(views, queries.get(view_id), tuple_level);
		}		
		
		Vector<Query> user_query = new Vector<Query>();
		
		user_query.add(queries.get(view_id));
		
		output_queries(user_query, path2 + "user_queries");
		
		output_queries(views, path2 + "views");
		
		Vector<Query> all_citation_queries = Query_operation.get_all_citation_queries(c2, pst);
		
		output_queries(all_citation_queries, path2 + "citation_query");
		
		Vector<String> sqls = new Vector<String>();
		
		sqls.add(Query_converter.datalog2sql(queries.get(view_id)));
		
		Query_operation.write2file(path2 + "user_query_sql", sqls);
		
		c1.close();
		
		c2.close();
		
//		Query query = query_storage.get_query_by_id(1);
		
		
	}
	
	static void output_queries(Vector<Query> queries, String file_name) throws IOException
	{
		Vector<String> query_strs = new Vector<String>();
		
		for(int i = 0; i<queries.size(); i++)
		{
			String query_str = Query_operation.covert2data_str(queries.get(i));
			
			query_strs.add(query_str);
		}
		
		Query_operation.write2file(file_name, query_strs);
	}
	
	
	static void stress_test(Vector<Query> views, Query query, boolean tuple_level) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
	{
		HashMap<Head_strs, HashSet<String> > citation_strs = new HashMap<Head_strs, HashSet<String>>();
		
		HashMap<Head_strs, HashSet<String> > citation_strs2 = new HashMap<Head_strs, HashSet<String>>();

		
		String f_name = new String();
		
		HashMap<Head_strs, Vector<Vector<Covering_set>>> citation_view_map1 = new HashMap<Head_strs, Vector<Vector<Covering_set>>>();

		HashMap<Head_strs, Vector<Vector<Covering_set>>> citation_view_map2 = new HashMap<Head_strs, Vector<Vector<Covering_set>>>();
		
		Vector<Vector<Covering_set>> citation_view2 = new Vector<Vector<Covering_set>>();

		
//		while(views.size() < view_max_size)
		if(tuple_level)
		{
			
			Connection c = null;
		      PreparedStatement pst = null;
			Class.forName("org.postgresql.Driver");
		    c = DriverManager
		        .getConnection(populate_db.db_url1, populate_db.usr_name , populate_db.passwd);
			
		    Tuple_level_approach_min.prepare_info = false;
		    
		    Tuple_level_approach_min.test_case = false;
		
			double end_time = 0;

			double middle_time = 0;
			
			double start_time = 0;
			
			HashSet<String> agg_citations = null;

			
			start_time = System.nanoTime();
						
			Tuple_level_approach_min.tuple_reasoning(query, c, pst);
			
			middle_time = System.nanoTime();
			
			Tuple_level_approach_min.prepare_citation_information(views, c, pst);
			
			agg_citations = Tuple_level_approach_min.tuple_gen_agg_citations(query, c, pst);
													
			end_time = System.nanoTime();
			
			double time = (end_time - start_time)*1.0;
			
			double agg_time = (end_time - middle_time) * 1.0/1000000000;
			
			double reasoning_time = (middle_time - start_time) * 1.0/1000000000;
			
			time = time /(times * 1000000000);
						
			System.out.print(Tuple_level_approach_min.group_num + "	");
			
			System.out.print(Tuple_level_approach_min.tuple_num + "	");
			
			System.out.print(time + "s	");
			
			System.out.print("total_exe_time::" + time + "	");
			
			System.out.print("reasoning_time::" + reasoning_time + "	");
			
			System.out.print("aggregation_time::" + agg_time + "	");
			
			System.out.print("covering set::" + Tuple_level_approach_min.covering_sets_query + "	");
			
			System.out.print("citation_size::" + Tuple_level_approach_min.covering_sets_query.c_vec.size() + "	");
			
			HashMap<String, HashSet<String>> view_strs = new HashMap<String, HashSet<String>>();
			
			int covering_set_size = Tuple_level_approach_min.compute_distinct_num_covering_sets(view_strs);
			
			System.out.print("distinct_covering_set_size::" + covering_set_size + "	");
			
			Set<String> view_name = view_strs.keySet();
			
			int diff_view_num = 0;
			
			for(Iterator iter = view_name.iterator(); iter.hasNext();)
			{
				String curr_v_name = (String)iter.next();
				
				HashSet<String> curr_views = view_strs.get(curr_v_name);
				
				diff_view_num += curr_views.size();
			}
			
			System.out.print("distinct_view_size::" + diff_view_num + "	");
			
//			Set<Head_strs> h_l = Tuple_reasoning1_full_min_test.head_strs_rows_mapping.keySet();
//			
//			double citation_size1 = 0;
//			
//			int row = 0;
//			
//			start_time = System.nanoTime();
//			
//			for(Iterator iter = h_l.iterator(); iter.hasNext();)
//			{
//				Head_strs h_value = (Head_strs) iter.next();
//				
//				HashSet<String> citations = Tuple_reasoning1_full_min_test.gen_citation(h_value, c, pst);
//				
//				citation_size1 += citations.size();
//				
////				System.out.println(citations);
//				
//				row ++;
//				
//				if(row >= 10)
//					break;
//				
//			}
//			
//			end_time = System.nanoTime();
//			
//			if(row !=0)
//			citation_size1 = citation_size1 / row;
//			
//			time = (end_time - start_time)/(row * 1.0 * 1000000000);
//			
//			System.out.print(time + "s	");
//			
//			System.out.print(citation_size1 + "	");
//			
//			System.out.print(row + "	");
			
//			Set<Head_strs> head = citation_view_map1.keySet();
//			
//			int row_num = 0;
//			
//			double origin_citation_size = 0.0;
//			
//			for(Iterator iter = head.iterator(); iter.hasNext();)
//			{
//				Head_strs head_val = (Head_strs) iter.next();
//				
//				Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
//				
//				row_num++;
//				
//				for(int p = 0; p<c_view.size(); p++)
//				{
//					origin_citation_size += c_view.get(p).size();
//				}
//				
//			}
//			
//			
//			
//			if(row_num !=0)
//				origin_citation_size = origin_citation_size / row_num;
//			
			
			
			System.out.print(Tuple_level_approach_min.covering_set_num * 1.0/Tuple_level_approach_min.tuple_num + "	");
			
			System.out.print("pre_processing::" + Tuple_level_approach_min.pre_processing_time + "	");
			
			System.out.print("query::" + Tuple_level_approach_min.query_time + "	");
			
			System.out.print("reasoning::" + Tuple_level_approach_min.reasoning_time + "	");
			
			System.out.print("population::" + Tuple_level_approach_min.population_time + "	");
			
			
			
//			time = (end_time - start_time) * 1.0/1000000000;
//			
//			System.out.print("Aggregation_time::" + time + "	");
//			
//			System.out.print("Aggregation_size::" + agg_citations.size() + "	");
			
//			start_time = System.nanoTime();
//			
//			Tuple_reasoning1_full_test.tuple_reasoning(query, c, pst);
//			
//			agg_citations = Tuple_reasoning1_full_test.tuple_gen_agg_citations(query);
//						
//			end_time = System.nanoTime();
//			
//			time = (end_time - start_time) * 1.0/1000000000;
//			
//			System.out.print("total_reasoning_time::" + time + "	");
			
			Vector<String> agg_results = new Vector<String>();
			
			agg_results.add(Tuple_level_approach_min.covering_sets_query.toString());
			
			Query_operation.write2file(path + "covering_sets", agg_results);

//			for(int k = 0; k<Aggregation5.curr_res.size(); k++)
//			{
//				agg_results.add(Aggregation5.curr_res.get(k).toString());
//			}
			
//			String aggregate_result = Aggregation5.curr_res.toString();
			
//			System.out.println(aggregate_result);
						
			
//			HashSet<String> agg_citations2 = Tuple_reasoning1_full_test.tuple_gen_agg_citations2(query);
//			
//			System.out.println("citation1::" + agg_citations);
//			
//			System.out.println("citation2::" + agg_citations2);
//			
//			if(!agg_citations.equals(agg_citations2))
//			{
//				
//				for(int p = 0; p < Aggregation3.author_lists.size(); p ++)
//				{
//					HashMap<String, HashSet<String>> citation1 = Aggregation3.author_lists.get(p);
//					
//					HashMap<String, HashSet<String>> citation2 = Aggregation5.full_citations.get(p);
//					
//					HashSet<String> author1 = citation1.get("author");
//					
//					HashSet<String> author2 = citation2.get("author");
//					
//					if(!author1.equals(author2))
//					{
//						
//						HashSet<String> curr_authors = new HashSet<String>();
//						
//						curr_authors.addAll(author1);
//						
//						curr_authors.removeAll(author2);
//						
//						System.out.println(Aggregation3.curr_res.get(p));
//						
//						System.out.println(Aggregation5.curr_res.get(p));
//						
//						System.out.println(p);
//						
//						System.out.println(author1.size());
//						
//						System.out.println(author2.size());
//						
//						int y = 0;
//						
//						y++;
//					}
//				}
//				
//				Assert.assertEquals(true, false);
//			}
						
//			diff_agg_time(tuple_level, query);
			
			System.out.println();
			
			c.close();
			
//			System.out.println(agg_citations);
			
			
		}
		else
		{
			
			Connection c = null;
		      PreparedStatement pst = null;
			Class.forName("org.postgresql.Driver");
		    c = DriverManager
		        .getConnection(populate_db.db_url2, populate_db.usr_name , populate_db.passwd);
			
		    Semi_schema_level_approach_min.prepare_info = false;
		    
		    Semi_schema_level_approach_min.test_case = false;
		    
			double end_time = 0;

			double middle_time = 0;
			
			double start_time = 0;
			
			HashSet<String> agg_citations = null;
			
			
			start_time = System.nanoTime();
			
			Semi_schema_level_approach_min.tuple_reasoning(query, c, pst);
			
			middle_time = System.nanoTime();
			
			Semi_schema_level_approach_min.prepare_citation_information(views, c, pst);
			
			agg_citations = Semi_schema_level_approach_min.tuple_gen_agg_citations(query, c, pst);
													
			end_time = System.nanoTime();
			
			double time = (end_time - start_time)*1.0;
			
			double agg_time = (end_time - middle_time) * 1.0/1000000000;
			
			double reasoning_time = (middle_time - start_time) * 1.0/1000000000;
			
			time = time /(times * 1000000000);
						
			System.out.print(Semi_schema_level_approach_min.group_num + "	");
			
			System.out.print(Semi_schema_level_approach_min.tuple_num + "	");
			
			System.out.print(time + "s	");
			
			System.out.print("total_exe_time::" + time + "	");
			
			System.out.print("reasoning_time::" + reasoning_time + "	");
			
			System.out.print("aggregation_time::" + agg_time + "	");
			
			System.out.print("covering set::" + Semi_schema_level_approach_min.covering_sets_query + "	");
			
			System.out.print("citation_size::" + Semi_schema_level_approach_min.covering_sets_query.c_vec.size() + "	");
			
			HashMap<String, HashSet<String>> view_strs = new HashMap<String, HashSet<String>>();
			
			int covering_set_size = Semi_schema_level_approach_min.compute_distinct_num_covering_sets(view_strs);
			
			System.out.print("distinct_covering_set_size::" + covering_set_size + "	");
			
			Set<String> view_name = view_strs.keySet();
			
			int diff_view_num = 0;
			
			for(Iterator iter = view_name.iterator(); iter.hasNext();)
			{
				String curr_v_name = (String)iter.next();
				
				HashSet<String> curr_views = view_strs.get(curr_v_name);
				
				diff_view_num += curr_views.size();
			}
			
			System.out.print("distinct_view_size::" + diff_view_num + "	");
			
//			Set<Head_strs> h_l = Tuple_reasoning1_full_min_test.head_strs_rows_mapping.keySet();
//			
//			double citation_size1 = 0;
//			
//			int row = 0;
//			
//			start_time = System.nanoTime();
//			
//			for(Iterator iter = h_l.iterator(); iter.hasNext();)
//			{
//				Head_strs h_value = (Head_strs) iter.next();
//				
//				HashSet<String> citations = Tuple_reasoning1_full_min_test.gen_citation(h_value, c, pst);
//				
//				citation_size1 += citations.size();
//				
////				System.out.println(citations);
//				
//				row ++;
//				
//				if(row >= 10)
//					break;
//				
//			}
//			
//			end_time = System.nanoTime();
//			
//			if(row !=0)
//			citation_size1 = citation_size1 / row;
//			
//			time = (end_time - start_time)/(row * 1.0 * 1000000000);
//			
//			System.out.print(time + "s	");
//			
//			System.out.print(citation_size1 + "	");
//			
//			System.out.print(row + "	");
			
//			Set<Head_strs> head = citation_view_map1.keySet();
//			
//			int row_num = 0;
//			
//			double origin_citation_size = 0.0;
//			
//			for(Iterator iter = head.iterator(); iter.hasNext();)
//			{
//				Head_strs head_val = (Head_strs) iter.next();
//				
//				Vector<Vector<citation_view_vector>> c_view = citation_view_map1.get(head_val);
//				
//				row_num++;
//				
//				for(int p = 0; p<c_view.size(); p++)
//				{
//					origin_citation_size += c_view.get(p).size();
//				}
//				
//			}
//			
//			
//			
//			if(row_num !=0)
//				origin_citation_size = origin_citation_size / row_num;
//			
			
			
			System.out.print(Semi_schema_level_approach_min.covering_set_num * 1.0/Semi_schema_level_approach_min.tuple_num + "	");
			
			System.out.print("pre_processing::" + Semi_schema_level_approach_min.pre_processing_time + "	");
			
			System.out.print("query::" + Semi_schema_level_approach_min.query_time + "	");
			
			System.out.print("reasoning::" + Semi_schema_level_approach_min.reasoning_time + "	");
			
			System.out.print("population::" + Semi_schema_level_approach_min.population_time + "	");
			
			System.out.println();
			
			Vector<String> agg_results = new Vector<String>();
			
			agg_results.add(Semi_schema_level_approach_min.covering_sets_query.toString());
			
			Query_operation.write2file(path + "covering_sets", agg_results);

			c.close();
			
//			Tuple_reasoning1.compare(citation_view_map1, citation_view_map2);
			
//			Tuple_reasoning1.compare_citation(citation_strs, citation_strs2);
			
			
//						
//			reset();
//			
//			Vector<Query> queries = query_generator.gen_queries(j, size_range);
//			
//			Query query = query_generator.gen_query(j, c, pst);
			
//			System.out.println(queries.get(0));

			
//			Vector<String> relation_names = get_unique_relation_names(queries.get(0));
			
//			view_generator.generate_store_views(relation_names, num_views);
			
//			query_storage.store_query(queries.get(0), new Vector<Integer>());
			
			
		}
	}
	
	
	static void reset(Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		
		String query = "delete from user_query2conditions";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "delete from user_query2subgoals";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "delete from user_query_conditions";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		query = "delete from user_query_table";
		
		pst = c.prepareStatement(query);
		
		pst.execute();
		
		
	}

}