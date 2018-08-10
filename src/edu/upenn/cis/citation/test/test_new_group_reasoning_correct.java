package edu.upenn.cis.citation.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import edu.upenn.cis.citation.aggregation.Aggregation6;
import edu.upenn.cis.citation.citation_view.Head_strs;
import edu.upenn.cis.citation.citation_view.Head_strs2;
import edu.upenn.cis.citation.citation_view.citation_view;
import edu.upenn.cis.citation.citation_view.Covering_set;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.reasoning1.Tuple_level_approach;
import edu.upenn.cis.citation.reasoning1.Semi_schema_level_approach;
import edu.upenn.cis.citation.stress_test.query_generator;
import edu.upenn.cis.citation.stress_test.view_generator;
import edu.upenn.cis.citation.user_query.query_storage;

public class test_new_group_reasoning_correct {
	
	static int size_range = 100;
	
	static int times = 1;
	
	static int size_upper_bound = 5;
	
	static int num_views = 3;
	
	static int view_max_size = 40;
	
	static int upper_bound = 20;
	
	static int query_num = 8;
	
	static boolean store_covering_set = true;
	
	static Vector<String> relations = new Vector<String>();
	
	static String path = "reasoning_results/";
	
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
		
//		Connection c3 = null;
	      PreparedStatement pst = null;
		Class.forName("org.postgresql.Driver");
	    c1 = DriverManager
	        .getConnection(populate_db.db_url1, populate_db.usr_name , populate_db.passwd);
		
	    c2 = DriverManager
	        .getConnection(populate_db.db_url2, populate_db.usr_name , populate_db.passwd);
	    
//	    c3 = DriverManager
//		        .getConnection(populate_db.db_url3, populate_db.usr_name , populate_db.passwd);
		
//	    System.out.println(get_single_table_size("family", c, pst));
	    
	    
	    int k = Integer.valueOf(args[0]);
	    
	    int view_size = Integer.valueOf(args[1]);
	    
	    boolean new_query = Boolean.valueOf(args[2]);
		
		boolean new_rounds = Boolean.valueOf(args[3]);
		
		boolean tuple_level = Boolean.valueOf(args[4]);
		
		boolean schema_level = Boolean.valueOf(args[5]);
		
		boolean new_start = Boolean.valueOf(args[6]);
		
		boolean agg_intersection = Boolean.valueOf(args[7]);
		
		boolean new_approach = true;//Boolean.valueOf(args[8]);
		
		query_generator.query_result_size = 10000;
				
//		Query query = query_storage.get_query_by_id(1);
		query_generator.init_parameterizable_attributes(c2, pst);

		
//		for(int k = 3; k<=query_num; k++)
		{
			if(new_query)
			{
				System.out.println("new query");
				
				reset(c1, pst);
				
				reset(c2, pst);
			}
						
			Query query = null;
			
			try{
				query = query_storage.get_query_by_id(1, c2, pst);
			}
			catch(Exception e)
			{
				query = query_generator.gen_query(k, c2, pst);
				query_storage.store_query(query, new Vector<Integer>(), c2, pst);
				System.out.println(query);
			}
			
			if(new_query)
			{
				Vector<String> sqls = new Vector<String>();
				
				sqls.add(Query_converter.datalog2sql_test(query));
				
				Query_operation.write2file(path + "user_query_sql", sqls);
			}
			
			relations = get_unique_relation_names(query);
			
			Vector<Query> views = null;
			
			if(new_rounds)
			{
				
				populate_db.renew_table(c1, pst);
				
				get_table_size(relations, c1, pst);
				
				views = view_generator.generate_store_views_without_predicates(relations, view_size, query.body.size(), c1, c2, pst);//(relations, c1, c2, pst);
				
//				views = view_generator.generate_store_views_without_predicates(relation_names, view_size, query.body.size());
				
				for(Iterator iter = views.iterator(); iter.hasNext();)
				{
					Query view = (Query) iter.next();
					
					System.out.println(view);
				}
				
				get_table_size(relations, c1, pst);
			}
			else
			{
				views = view_operation.get_all_views(c2, pst);
				
				if(new_start)
				{
					System.out.println();
					
					view_generator.initial();
					
					view_generator.gen_one_additional_predicates(views, relations, query.body.size(), query, c1, c2, pst);
					
					for(Iterator iter = views.iterator(); iter.hasNext();)
					{
						Query view = (Query) iter.next();
						
						System.out.println(view);
					}
					
					get_table_size(relations, c1, pst);
				}
			}
			
			
			

			Vector<Query> all_citation_queries = Query_operation.get_all_citation_queries(c1, pst);
			
			c1.close();
			
			c2.close();
						
			stress_test(query, views, tuple_level, schema_level, agg_intersection, new_approach);

			
			Vector<Query> user_query = new Vector<Query>();
			
			user_query.add(query);
			
			output_queries(user_query, path + "user_queries");
			
			output_queries(views, path + "views");
			
			output_queries(all_citation_queries, path + "citation_query");
			
			
			
		}
		

		
		
		
		
		
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
	
	static boolean check_equality(HashSet<Covering_set> covering_set1, HashSet<Covering_set> covering_set2)
	{
	  if(covering_set1.size() != covering_set2.size())
	    return false;
	  
	  for(Covering_set c1 : covering_set1)
	  {
	    
	    boolean exist = false;
	    
	    for(Covering_set c2 : covering_set2)
	    {
	      if(c1.equals(c2))
	      {
	        exist = true;
	        
	        break;
	      }
	    }
	    
	    if(!exist)
	      return false;
	  }
	  
	  return true;
	}
	
	static boolean test_covering_set_equality(HashMap<int[], HashSet<Covering_set>> covering_set1, HashMap<String, HashSet<Covering_set>> covering_set2)
	{
	  
	  Set<int[]> keys1 = covering_set1.keySet();
	  
	  Set<String> keys2 = covering_set2.keySet();
	  
	  ArrayList<HashSet<Covering_set>> covering_set_list1 = new ArrayList<HashSet<Covering_set>>();
	  
	  ArrayList<HashSet<Covering_set>> covering_set_list2 = new ArrayList<HashSet<Covering_set>>();
	  
	  
	  for(int [] key1 : keys1)
	  {
	    HashSet<Covering_set> curr_covering_set = covering_set1.get(key1);
	    
	    
	    covering_set_list1.add(curr_covering_set);
	  }
	  
	  
	  for(String key2 : keys2)
	  {
	    HashSet<Covering_set> curr_covering_set = covering_set2.get(key2);
	    covering_set_list2.add(curr_covering_set);
	  }
	  
	  if(covering_set_list1.size() != covering_set_list2.size())
	    return false;
	  
	  for(HashSet<Covering_set> list1 : covering_set_list1)
	  {
	    boolean exist = false;
	    
	    for(HashSet<Covering_set> list2 : covering_set_list2)
	    {
	      if(check_equality(list1, list2))
	      {
	        exist = true;
	        
	        break;
	      }
	    }
	    
	    if(!exist)
	      return false;
	  }
	  
	  
	  return true;
	}
	
	   static boolean test_covering_set_equality2(HashMap<String, HashSet<Covering_set>> covering_set1, HashMap<String, HashSet<Covering_set>> covering_set2)
	    {
	      
	      Set<String> keys1 = covering_set1.keySet();
	      
	      Set<String> keys2 = covering_set2.keySet();
	      
	      ArrayList<HashSet<Covering_set>> covering_set_list1 = new ArrayList<HashSet<Covering_set>>();
	      
	      ArrayList<HashSet<Covering_set>> covering_set_list2 = new ArrayList<HashSet<Covering_set>>();
	      
	      
	      for(String key1 : keys1)
	      {
	        HashSet<Covering_set> curr_covering_set = covering_set1.get(key1);
	        
	        
	        covering_set_list1.add(curr_covering_set);
	      }
	      
	      
	      for(String key2 : keys2)
	      {
	        HashSet<Covering_set> curr_covering_set = covering_set2.get(key2);
	        covering_set_list2.add(curr_covering_set);
	      }
	      
	      if(covering_set_list1.size() != covering_set_list2.size())
	        return false;
	      
	      for(HashSet<Covering_set> list1 : covering_set_list1)
	      {
	        boolean exist = false;
	        
	        for(HashSet<Covering_set> list2 : covering_set_list2)
	        {
	          if(check_equality(list1, list2))
	          {
	            exist = true;
	            
	            break;
	          }
	        }
	        
	        if(!exist)
	          return false;
	      }
	      
	      
	      return true;
	    }
	
	static boolean check_covering_sets_schema_level_equality(ArrayList<Covering_set> covering_sets1, ArrayList<Covering_set> covering_sets2)
	{
	  if(covering_sets1.size() != covering_sets2.size())
	    return false;
	  
	  
	  for(Covering_set covering_set1: covering_sets1)
	  {
	    boolean exist = false;
	    
	    for(Covering_set covering_set2: covering_sets2)
	    {
	      if(covering_set1.equals(covering_set2))
	      {
	        exist = true;
	        
	        break;
	      }
	    }
	    
	    if(!exist)
	      return false;
	    
	  }
	  
	  return true;
	  
	}
	
	static void stress_test(Query query, Vector<Query> views, boolean tuple_level, boolean schema_level, boolean agg_intersection, boolean new_approach) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
	{
		HashMap<Head_strs, HashSet<String> > citation_strs = new HashMap<Head_strs, HashSet<String>>();
		
		HashMap<Head_strs, HashSet<String> > citation_strs2 = new HashMap<Head_strs, HashSet<String>>();

		
		String f_name = new String();
		
		HashMap<Head_strs, Vector<Vector<Covering_set>>> citation_view_map1 = new HashMap<Head_strs, Vector<Vector<Covering_set>>>();

		HashMap<Head_strs, Vector<Vector<Covering_set>>> citation_view_map2 = new HashMap<Head_strs, Vector<Vector<Covering_set>>>();
		
		Vector<Vector<Covering_set>> citation_view2 = new Vector<Vector<Covering_set>>();

		
//		while(views.size() < view_max_size)
		if(new_approach)
		{
			
			Connection c1 = null;
		      PreparedStatement pst = null;
			Class.forName("org.postgresql.Driver");
		    c1 = DriverManager
		        .getConnection(populate_db.db_url1, populate_db.usr_name , populate_db.passwd);
		    
		    Connection c2 = DriverManager
                .getConnection(populate_db.db_url2, populate_db.usr_name , populate_db.passwd);
			
		    Semi_schema_level_approach.prepare_info = false;
		
		    Semi_schema_level_approach.agg_intersection = agg_intersection;
		    
          Tuple_level_approach.prepare_info = false;
      
          Tuple_level_approach.agg_intersection = agg_intersection;
		    
		    double end_time = 0;

			double middle_time = 0;
			
			double start_time = 0;
			
			double time1 = 0;
			
			HashSet<String> agg_citations = null;
						
			start_time = System.nanoTime();
			
			Semi_schema_level_approach.tuple_reasoning(query, c2, pst);
			
			ArrayList<HashSet<citation_view>> views_per_group = Semi_schema_level_approach.cal_covering_sets_schema_level(query, c2, pst);

            ArrayList<Covering_set> covering_sets_schema_level = new ArrayList<Covering_set>();
            
            covering_sets_schema_level.addAll(Aggregation6.curr_res);
            
            Semi_schema_level_approach.prepare_citation_information(c2, pst);
            
            agg_citations = Semi_schema_level_approach.gen_citation_schema_level(views_per_group, c2, pst);
			
			end_time = System.nanoTime();
			
			start_time = System.nanoTime();
            
            Tuple_level_approach.tuple_reasoning(query, c1, pst);
            
            Tuple_level_approach.prepare_citation_information(c1, pst);

            
            end_time = System.nanoTime();
			
			time1 = (end_time - start_time) * 1.0/1000000000;
			
			
			
			
			
			ArrayList<HashSet<citation_view>> views_per_group2 = Tuple_level_approach.cal_covering_sets_schema_level(query, c1, pst);
			
			ArrayList<Covering_set> covering_sets_schema_level2 = new ArrayList<Covering_set>();
			
			covering_sets_schema_level2.addAll(Aggregation6.curr_res);
			
//			middle_time = System.nanoTime();
//			
			
			
			HashSet<String> agg_citations2 = Tuple_level_approach.gen_citation_schema_level(views_per_group2, c1, pst);

			System.out.println(covering_sets_schema_level);
			
			System.out.println(covering_sets_schema_level2);
			
			System.out.println(agg_citations);
			
			System.out.println(agg_citations2);
			
	        System.out.println(test_covering_set_equality2(Semi_schema_level_approach.c_view_map, Tuple_level_approach.c_view_map));
			
			System.out.println(agg_citations.equals(agg_citations2));
			
			System.out.println(check_covering_sets_schema_level_equality(covering_sets_schema_level, covering_sets_schema_level2));
			
			//			
//			 Tuple_reasoning1_full_test_opt_copy.tuple_gen_agg_citations(query, c, pst);
//													
//			end_time = System.nanoTime();
//			
//			double time = (end_time - start_time)*1.0;
//			
//			double agg_time = (middle_time - time1) * 1.0/1000000000;
//			
//			double reasoning_time = (middle_time - start_time) * 1.0/1000000000;
//			
//			double citation_gen_time = (end_time - middle_time) * 1.0/1000000000;
//			
//			time = time /(times * 1000000000);
//						
			System.out.print(Semi_schema_level_approach.group_num + "	");
			
			System.out.print(Tuple_level_approach.group_num + "  ");
//			
//			System.out.print(Tuple_reasoning1_full_test_opt.tuple_num + "	");
//			
//			System.out.print(time + "s	");
//			
//			System.out.print("total_exe_time::" + time + "	");
//			
//			System.out.print("reasoning_time::" + reasoning_time + "	");
//			
//			System.out.print("aggregation_time::" + agg_time + "	");
//			
//			System.out.print("citation_gen_time::" + citation_gen_time + "	");
//			
////			System.out.print("covering_sets::" + Aggregation5.curr_res + "	");
//			
//			System.out.print("covering_set_size::" + Aggregation5.curr_res.size() + "	");
//			
//			int distinct_view_size = Aggregation5.cal_distinct_views();
//			
//			System.out.print("distinct_view_size::" + distinct_view_size + "	");
//			
//			
//			System.out.print(Tuple_reasoning1_full_test_opt.covering_set_num * 1.0/Tuple_reasoning1_full_test_opt.tuple_num + "	");
//			
//			System.out.print("pre_processing::" + Tuple_reasoning1_full_test_opt.pre_processing_time + "	");
//			
//			System.out.print("query::" + Tuple_reasoning1_full_test_opt.query_time + "	");
//			
//			System.out.print("reasoning::" + Tuple_reasoning1_full_test_opt.reasoning_time + "	");
//			
//			System.out.print("population::" + Tuple_reasoning1_full_test_opt.population_time + "	");
			
			
			
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
			
//			Vector<String> agg_results = new Vector<String>();
//			
//			agg_results.add(Tuple_reasoning1_full_min_test.covering_sets_query.toString());
//			
//			Query_operation.write2file(path + "covering_sets", agg_results);

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
			
//			System.out.println(agg_citations.toString());
			
			c1.close();
			
//			System.out.println(agg_citations);
			
			
		}
//		else
//		{
//
//          
//          Connection c1 = null;
//            PreparedStatement pst = null;
//          Class.forName("org.postgresql.Driver");
//          c1 = DriverManager
//              .getConnection(populate_db.db_url1, populate_db.usr_name , populate_db.passwd);
//          
//          Tuple_reasoning1_full_test_opt.prepare_info = false;
//      
//          Tuple_reasoning1_full_test_opt.agg_intersection = agg_intersection;
//          
//        Tuple_reasoning1_full_test_opt_copy.prepare_info = false;
//    
//        Tuple_reasoning1_full_test_opt_copy.agg_intersection = agg_intersection;
//          
//          double end_time = 0;
//
//          double middle_time = 0;
//          
//          double start_time = 0;
//          
//          double time2 = 0;
//          
//          HashSet<String> agg_citations = null;
//                      
//          
//          start_time = System.nanoTime();
//          
//          Tuple_reasoning1_full_test_opt_copy.tuple_reasoning(query, c1, pst);
//          
//          
//          end_time = System.nanoTime();
//          
//          time2 = (end_time - start_time) * 1.0/1000000000;
//          
//          
//          System.out.println("time2::" + time2);
//          
//          System.out.print(Tuple_reasoning1_full_test_opt_copy.group_num + "   ");
//          
//          System.out.println();
//          
//          c1.close();
//          
//		}
			
			
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