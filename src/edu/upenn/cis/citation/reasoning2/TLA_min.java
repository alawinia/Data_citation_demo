package edu.upenn.cis.citation.reasoning2;
import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static org.junit.Assert.*;


import edu.upenn.cis.citation.Corecover.*;
import edu.upenn.cis.citation.Operation.Conditions;
import edu.upenn.cis.citation.Operation.Operation;
import edu.upenn.cis.citation.Operation.op_equal;
import edu.upenn.cis.citation.Operation.op_greater;
import edu.upenn.cis.citation.Operation.op_greater_equal;
import edu.upenn.cis.citation.Operation.op_less;
import edu.upenn.cis.citation.Operation.op_less_equal;
import edu.upenn.cis.citation.Operation.op_not_equal;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;
import edu.upenn.cis.citation.aggregation.Aggregation6;
import edu.upenn.cis.citation.citation_view.*;
import edu.upenn.cis.citation.covering_sets_calculation.Covering_sets_clustering_by_relation;
import edu.upenn.cis.citation.covering_sets_calculation.Covering_sets_no_clustering;
import edu.upenn.cis.citation.covering_sets_calculation.Join_covering_sets;
import edu.upenn.cis.citation.data_structure.IntList;
import edu.upenn.cis.citation.data_structure.Query_lm_authors;
import edu.upenn.cis.citation.data_structure.StringList;
import edu.upenn.cis.citation.data_structure.Unique_StringList;
import edu.upenn.cis.citation.datalog.Parse_datalog;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.examples.Load_views_and_citation_queries;
import edu.upenn.cis.citation.gen_citation.gen_citation0;
import edu.upenn.cis.citation.output.output2excel;
import edu.upenn.cis.citation.schema_level_reasoning.Schema_reasoning_with_agg;
import edu.upenn.cis.citation.user_query.query_storage;
import sun.util.resources.cldr.ur.CurrencyNames_ur;

public class TLA_min {
		
	public static HashMap<String, Integer> max_author_num = new HashMap<String, Integer>();
			
//	static HashMap<String, Query> view_mapping = new HashMap<String, Query>();
	
	static Vector<Conditions> valid_conditions = new Vector<Conditions> ();
	
	static Vector<Lambda_term> valid_lambda_terms = new Vector<Lambda_term>();
	
	static HashMap<String, Integer> condition_id_mapping = new HashMap<String, Integer>();
	
	public static HashMap<String, Integer> lambda_term_id_mapping = new HashMap<String, Integer>();
	
	static HashMap<Conditions, ArrayList<Tuple>> conditions_map = new HashMap<Conditions, ArrayList<Tuple>>();
	
	static HashMap<String, ArrayList<String>> lambda_terms_map = new HashMap<String, ArrayList<String>>();
	
	static HashMap<String, HashSet<Integer>> relation_arg_mapping = new HashMap<String, HashSet<Integer>>();
//	static HashMap<String, ArrayList<Tuple>> tuple_mapping = new HashMap<String, ArrayList<Tuple>>();
							
	static HashMap<Tuple, Double> view_mapping_cost = new HashMap<Tuple, Double>();
	
	static String file_name = "tuple_level.xlsx";
	
	static HashMap<Head_strs, Vector<HashSet<String>>> authors = new HashMap<Head_strs, Vector<HashSet<String>>>();
	
	public static ArrayList<Lambda_term[]> query_lambda_str = new ArrayList<Lambda_term[]>();
	
//	static StringList view_list = new StringList();
	
	static HashMap<String, HashMap<String, String>> view_query_mapping = new HashMap<String, HashMap<String, String>>();
	
	public static IntList query_ids = new IntList();
		
	public static HashMap<String, ArrayList<ArrayList<String>>> author_mapping = new HashMap<String, ArrayList<ArrayList<String>>>(); 
	
	public static HashMap<String, Covering_set> c_view_map = new HashMap<String, Covering_set>();
	
	public static HashMap<String, HashSet<Integer>> signiture_rid_mappings = new HashMap<String, HashSet<Integer>>();
	
	public static HashMap<String, HashSet<Tuple>> signiture_view_mappings_mappings = new HashMap<String, HashSet<Tuple>>();
	
	public static Covering_set covering_set_schema_level = null;
	
    public static HashSet<Tuple> valid_view_mapping_schema_level = null;
	
	public static ResultSet rs = null;
	
	public static int covering_set_num = 0;
	
	static long start = 0;
	
	static long end = 0;
	
	public static double pre_processing_time = 0.0;
	
	public static double query_time = 0.0;
	
	public static double reasoning_time = 0.0;
	
	public static double population_time = 0.0;
	
	public static double aggregation_time = 0.0;
	
	public static long second2nano = 1000000000;
	
	static Tuple [] viewtuples = null;
	
	public static StringList view_list = new StringList();
	
	public static HashMap<Head_strs, ArrayList<Integer>> head_strs_rows_mapping = new HashMap<Head_strs, ArrayList<Integer>>();
	
	public static int Resultset_prefix_col_num = 0;
	
	static HashMap<String, ArrayList<Tuple>> view_tuple_mapping = new HashMap<String, ArrayList<Tuple>>();
	
	static ArrayList<Tuple>[] head_variable_view_mapping = null;
	
	static HashMap<String, ArrayList<Integer>> head_variable_query_mapping = new HashMap<String, ArrayList<Integer>>();
	
//	static HashMap<String, ArrayList<String>> 
	
//	public static HashMap<String, ArrayList<Tuple>> tuple_mapping = new HashMap<String, ArrayList<Tuple>>(); 
	
	public static int tuple_num = 0;
	
	public static int group_num = 0;
	
	public static HashMap<String, Query> citation_queries = new HashMap<String, Query>();
	
	public static boolean prepare_info = true;
	
	public static boolean agg_intersection = true;
	
	public static boolean test_case = false;
	
	public static HashSet<Tuple> viewTuples = new HashSet<Tuple>();
	
	static HashMap<String, Integer> q_subgoal_id = new HashMap<String, Integer>();
	
	static HashSet<Integer> tuple_id_list = null;
	
	public static double covering_set_time = 0.0;
	
	public static boolean isclustering = true;
	
	static HashMap<Tuple, HashSet<String>> single_subgoal_relations = new HashMap<Tuple, HashSet<String>>();
	
	static HashMap<Tuple, HashSet<Argument>> single_view_head_args = new HashMap<Tuple, HashSet<Argument>>();
	
	static HashMap<Tuple, Integer> view_mapping_id_mappings = new HashMap<Tuple, Integer>();
	
	public static HashMap<Tuple, Integer> count_view_mapping_valid_row_numbers()
    {
      Set<String> signatures = signiture_view_mappings_mappings.keySet();
      
      HashMap<Tuple, Integer> view_mapping_count_mappings = new HashMap<Tuple, Integer>();
      
      for(String signature: signatures)
      {
        HashSet<Tuple> view_mappings = signiture_view_mappings_mappings.get(signature);
        
        HashSet<Integer> rids = signiture_rid_mappings.get(signature);
        
        
        for(Tuple view_mapping: view_mappings)
        {
          if(view_mapping_count_mappings.get(view_mapping) == null)
          {
            view_mapping_count_mappings.put(view_mapping, rids.size());
          }
          else
          {
            int old_count = view_mapping_count_mappings.get(view_mapping);
            
            old_count += rids.size();
            
            view_mapping_count_mappings.put(view_mapping, old_count);
            
          }
        }
        
        
      }
      
      return view_mapping_count_mappings;
      
    }
	
	public static void main(String [] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException, JSONException
	{
      Connection c = null;
      PreparedStatement pst = null;
    Class.forName("org.postgresql.Driver");
    c = DriverManager
        .getConnection(populate_db.db_url1, populate_db.usr_name , populate_db.passwd);
      
    String view_file = args[0];
    
    String query_file = args[1];
    
    populate_db.set_test_file_name(view_file);
    
    populate_db.initial(true, c, pst);
    
      Vector<Query> queries = Load_views_and_citation_queries.get_views(query_file, c, pst);
      
      Query query = queries.get(0);
      
//      tuple_reasoning(query, c, pst);
      
      System.out.println(valid_view_mapping_schema_level);
      
      c.close();
	}
	
	public static void compare_authors(HashMap<Head_strs, Vector<HashSet<String>>> authors1, HashMap<Head_strs, Vector<HashSet<String>>> authors2)
	{
		Set<Head_strs> key_set = authors1.keySet();
		
		for(Iterator h_val_iter = key_set.iterator(); h_val_iter.hasNext();)
		{
			Head_strs h_val = (Head_strs) h_val_iter.next();
	
			Vector<HashSet<String>> a1 = authors1.get(h_val);
			
			Vector<HashSet<String>> a2 = authors2.get(h_val);
			
			for(int i = 0; i<a2.size(); i++)
			{
				if(a2.get(i).isEmpty())
				{
					a2.remove(i);
					
					i--;
				}
			}
			
			HashSet<HashSet<String>> author_s1 = new HashSet<HashSet<String>>();
			
			HashSet<HashSet<String>> author_s2 = new HashSet<HashSet<String>>();
			
			for(int k = 0; k<a1.size(); k++)
			{
				author_s1.add(a1.get(k));
			}
			
			for(int k = 0; k<a2.size(); k++)
			{
				author_s2.add(a2.get(k));
			}
			
			if(!author_s1.equals(author_s2))
				assertEquals(1, 0);
			
			
//			System.out.println(h_val);
			
//			do_compare_authors(a1, a2);
		}
	}
	
	
	static void do_compare_authors(Vector<HashSet<String>> a1, Vector<HashSet<String>> a2)
	{
		int i = 0;
		
		int j = 0;
		
		if(a1.size() != a2.size())
		{
			assertEquals(1, 0);
		}
		
		for(i = 0; i<a1.size(); i++)
		{
			
			HashSet<String> a_list1 = a1.get(i);
			
			for(j = 0; j<a2.size(); j++)
			{
				HashSet<String> a_list2 = a2.get(j);
				
				if(a_list1.equals(a_list2))
					break;
				
			}
			
			if(j >= a2.size())
			{
				System.out.println(i + ":::" + j);
				
				System.out.println(a1.get(i));
				
				System.out.println(a2.get(i));
				
				
				
				
				
				assertEquals(1, 0);
			}
		}
	}
	
	public static void compare(HashMap<Head_strs, Vector<Vector<Covering_set>>> citation_view_map1, HashMap<Head_strs, Vector<Vector<Covering_set>>> citation_view_map2)
	{
		
		System.out.println("COMPARE::::");
		
		Set<Head_strs> key_set = citation_view_map1.keySet();
		
		for(Iterator h_val_iter = key_set.iterator(); h_val_iter.hasNext();)
		{
			Head_strs h_val = (Head_strs) h_val_iter.next();
	
			Vector<Vector<Covering_set>> c_views1 = citation_view_map1.get(h_val);
			
			Vector<Vector<Covering_set>> c_views2 = citation_view_map2.get(h_val);
			
//			System.out.println(h_val);
//			
//			System.out.println(c_views1);
//			
//			System.out.println(c_views2);
			
			compare(c_views1, c_views2);
		}
		
		
		
	}
	
	public static void compare_citation(HashMap<Head_strs, HashSet<String>> citation_view_map1, HashMap<Head_strs, HashSet<String>> citation_view_map2)
	{
		
		System.out.println("COMPARE::::");
		
		Set<Head_strs> key_set = citation_view_map1.keySet();
		
		for(Iterator h_val_iter = key_set.iterator(); h_val_iter.hasNext();)
		{
			Head_strs h_val = (Head_strs) h_val_iter.next();
	
			HashSet<String> c_views1 = citation_view_map1.get(h_val);
			
			HashSet<String> c_views2 = citation_view_map2.get(h_val);
			
			System.out.println(h_val.toString());
			
			if(!c_views1.equals(c_views2))
			{
				assertEquals(1, 0);

			}
			
//			compare_citation(c_views1, c_views2);
		}
		
		
		
	}
//	static void compare_citation(HashSet<String> c_views, HashSet<String> c_views1)
//	{
//		for(int i = 0; i<c_views.size(); i++)
//		{
//			int j = 0;
//			
////			System.out.println(c_views.get(i));
//			
//			for(j = 0; j<c_views1.size(); j++)
//			{
//				if(c_views.get(i).equals(c_views1.get(j)))
//					break;
//			}
//			
//			if(j >= c_views1.size())
//			{
////				System.out.println("errrror");
////				
////				System.out.println(c_views.get(i).toString());
////				
////				System.out.println(c_views1.get(0).toString());
//				
//			}
//		}
//	}
	
	
//	public static void compare(HashMap<Head_strs, Vector<String>> citation_view_map1, HashMap<Head_strs, Vector<String>> citation_view_map2)
//	{
//		
//		System.out.println("COMPARE::::");
//		
//		Set<Head_strs> key_set = citation_view_map1.keySet();
//		
//		for(Iterator h_val_iter = key_set.iterator(); h_val_iter.hasNext();)
//		{
//			Head_strs h_val = (Head_strs) h_val_iter.next();
//	
//			Vector<String> c_views1 = citation_view_map1.get(h_val);
//			
//			Vector<String> c_views2 = citation_view_map2.get(h_val);
//			
////			System.out.println(h_val.toString());
//			
//			compare(c_views1, c_views2);
//		}
//		
//		
//		
//	}
	
	static void compare(Vector<Vector<Covering_set>> c_views, Vector<Vector<Covering_set>> c_views1)
	{
		for(int i = 0; i<c_views.size(); i++)
		{
			int j = 0;
			
//			System.out.println(c_views.get(i));
			
			for(j = 0; j<c_views1.size(); j++)
			{
				if(do_compare(c_views.get(i), c_views1.get(j)))
					break;
			}
			
			if(j >= c_views1.size())
			{
				assertEquals(1, 0);
//				System.out.println("errrror");
//				
//				System.out.println(c_views.get(i).toString());
//				
//				System.out.println(c_views1.get(0).toString());
				
				assertEquals(1, 0);
			}
		}
	}
	
	
	static boolean do_compare(Vector<Covering_set> c1, Vector<Covering_set> c2)
	{
		if(c1.size() != c2.size())
			return false;
		
		for(int i = 0; i<c1.size(); i++)
		{
			int j = 0;
			
//			System.out.println("id1:::" + c1.get(i).index_str);

			
			for(j = 0; j<c2.size(); j++)
			{
				
				
//				System.out.println("id2:::" + c2.get(j).index_str);
				
				if(c1.get(i).equals(c2.get(j)))
				{
					break;
				}
			}
			if(j >= c2.size())
			{
//				System.out.println("id1:::" + c1.get(i));
				return false;
			}
		}
		return true;
	}
	
	
	
//	public static Query gen_query() throws SQLException, ClassNotFoundException
//	{
//		Class.forName("org.postgresql.Driver");
//        Connection c = DriverManager
//           .getConnection(populate_db.db_url,
//       	        populate_db.usr_name,populate_db.passwd);
//		
//        PreparedStatement pst = null;
//        
//		Vector<Argument> head_args = new Vector<Argument>();
//		
//		head_args.add(new Argument("introduction_family_id", "introduction"));
//		
//		head_args.add(new Argument("object_in_gtip", "object"));
//				
//		head_args.add(new Argument("object_structural_info_comments", "object"));
//		
//		head_args.add(new Argument("object_only_iuphar", "object"));
//		
//		head_args.add(new Argument("object_in_cgtp", "object"));
//		
//		head_args.add(new Argument("object_grac_comments", "object"));
//		
////		head_args.add(new Argument("gpcr_object_id", "gpcr"));
//		
//		
//		
////		head_args.add(new Argument("gpcr1_object_id", "object"));
//////		
////		head_args.add(new Argument("object_object_id", "gpcr"));
//				
//		
////		head_args.add(new Argument("family3_family_id", "family3"));
//		
////		head_args.add(new Argument("introduction_family_id", "introduction"));
//
//		
////		head_args.add(new Argument("introduction1_family_id", "introduction1"));
//////				
////		head_args.add(new Argument("introduction2_family_id", "introduction2"));
//		
////		head_args.add(new Argument("introduction3_family_id", "introduction3"));
//		
//		Subgoal head = new Subgoal("q", head_args);
//		
//		Vector<Subgoal> subgoals = new Vector<Subgoal>();
//		
////		Vector<Argument> args1 = view_operation.get_full_schema("gpcr", "gpcr", c, pst);
//		
//		Vector<Argument> args2 = view_operation.get_full_schema("introduction", "introduction", c, pst);
//		
//		
////		Vector<Argument> args6 = view_operation.get_full_schema("family2", "family", c, pst);
//		
////		Vector<Argument> args7 = view_operation.get_full_schema("family3", "family", c, pst);
//		
//		Vector<Argument> args3 = view_operation.get_full_schema("object", "object", c, pst);
//		
////		Vector<Argument> args4 = view_operation.get_full_schema("introduction1", "introduction", c, pst);
//////		
////		Vector<Argument> args5 = view_operation.get_full_schema("introduction2", "introduction", c, pst);
//		
////		Vector<Argument> args8 = view_operation.get_full_schema("introduction3", "introduction", c, pst);
//
//		
////		subgoals.add(new Subgoal("gpcr", args1));
//		
//		subgoals.add(new Subgoal("introduction", args2));
////		
////		subgoals.add(new Subgoal("family2", args6));
//		
////		subgoals.add(new Subgoal("family3", args7));
//				
//		subgoals.add(new Subgoal("object", args3));
//		
////		subgoals.add(new Subgoal("gpcr", args4));
////		
////		subgoals.add(new Subgoal("introduction2", args5));
//		
////		subgoals.add(new Subgoal("introduction3", args8));
//		
//		Vector<Conditions> conditions = new Vector<Conditions>();
//		
////		conditions.add(new Conditions(new Argument("object_id", "gpcr"), "gpcr", new op_less_equal(), new Argument("5"), new String()));
////		
//		conditions.add(new Conditions(new Argument("family_id", "introduction"), "introduction", new op_less_equal(), new Argument("3"), new String()));
////		
////		conditions.add(new Conditions(new Argument("family_id", "family2"), "family2", new op_less_equal(), new Argument("2"), new String()));
//		
////		conditions.add(new Conditions(new Argument("family_id", "family3"), "family3", new op_less_equal(), new Argument("2"), new String()));
////		
//		conditions.add(new Conditions(new Argument("object_id", "object"), "object", new op_less_equal(), new Argument("6"), new String()));
//		
////		conditions.add(new Conditions(new Argument("family_id", "introduction1"), "introduction1", new op_less_equal(), new Argument("2"), new String()));
////		
////		conditions.add(new Conditions(new Argument("family_id", "introduction2"), "introduction2", new op_less_equal(), new Argument("2"), new String()));
//
////		conditions.add(new Conditions(new Argument("family_id", "introduction3"), "introduction3", new op_less_equal(), new Argument("2"), new String()));
//
//		
////		conditions.add(new Conditions(new Argument("in_gtip", "object"), "object", new op_equal(), new Argument("'true'"), new String()));
//		
//		HashMap<String, String> subgoal_name_mapping = new HashMap<String, String>();
//		
//		subgoal_name_mapping.put("object", "object");
//		
//		subgoal_name_mapping.put("introduction", "introduction");
////		
////		subgoal_name_mapping.put("gpcr", "gpcr");
//		
////		subgoal_name_mapping.put("family3", "family");
//				
////		subgoal_name_mapping.put("introduction", "introduction");
//		
////		subgoal_name_mapping.put("introduction1", "introduction");
//////
////		subgoal_name_mapping.put("introduction2", "introduction");
//		
////		subgoal_name_mapping.put("introduction3", "introduction");
//
//
//		
//		Query q = new Query("q", head, subgoals, new Vector<Lambda_term>(), conditions, subgoal_name_mapping);
//		
////		System.out.println(q);
//		
//		c.close();
//		
//		return q;
//		
//		
//	}
//	
	
//	public static Vector<String> tuple_gen_agg_citations(Vector<Vector<citation_view_vector2>> c_views) throws ClassNotFoundException, SQLException, JSONException
//	{
//		Vector<Vector<citation_view_vector2>> agg_res = Aggregation1.aggregate(c_views);
//		
//		Vector<String> citation_aggs = new Vector<String>();
//		
//		for(int i = 0; i<agg_res.size(); i++)
//		{
////			output_vec_com(agg_res.get(i));
//			
//			String str = gen_citation1.get_citation_agg(agg_res.get(i), max_author_num, view_query_mapping, query_lambda_str, author_mapping);
//			
//			citation_aggs.add(str);
////			
//			System.out.print(agg_res.get(i).get(0).toString() + ":");
//			
//			System.out.println(str);
//
//		}
//		
//		return citation_aggs;
//	}
	
	public static HashSet<String> tuple_gen_citation_per_tuple(Vector<String> names, Connection c, PreparedStatement pst) throws JSONException, SQLException
	{
	  Head_strs head = new Head_strs(names);
	  
	  ArrayList<Integer> id_lists = head_strs_rows_mapping.get(head);
	  
	  Set<String> keys = c_view_map.keySet();

	  HashSet<String> citations = new HashSet<String>();
	  
	  Iterator<String> iter = keys.iterator();
	  
	  int i = 0;
	  
	    
	    while(iter.hasNext())
	    {
	      String curr_tuple_key = iter.next();
	      
	      HashSet<Integer> rids = signiture_rid_mappings.get(curr_tuple_key);
	      
	      Covering_set c_views = c_view_map.get(curr_tuple_key);
	      
	      for(i = 0; i<id_lists.size(); i++)
	      {
	        int tuple_id = id_lists.get(i);
	        
	        if(rids.contains(tuple_id))
	          {
	            
	            rs.absolute(tuple_id);
	            
	            HashSet<String> curr_citations = gen_citations_per_covering_set(c_views, rs, Resultset_prefix_col_num, tuple_id, c, pst);
	            
	            citations.addAll(curr_citations);
	            
//	            i++;
//	           
//	            if(i >= id_lists.size())
//	                break;
//	            
//	            tuple_id = id_lists.get(i);
	            
	          }
	        
	      }
	      
	      
	      
//	      if(i >= id_lists.size())
//	        break;
	    }
	    
	  return citations;
	}
	
//	public static HashSet<String> tuple_gen_agg_citations(Query query, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException, JSONException
//	{
//		start = System.nanoTime();
//		
//		Aggregation6.clear();
//		
//		int start_pos = Resultset_prefix_col_num;
//	    
//	    //rs, c_view_map, start_pos, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst
//		HashSet<String> citations = null;
//		
//		if(agg_intersection)
//			citations = Aggregation6.do_agg_intersection0(rs, c_view_map, start_pos, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
//		else
//			citations = Aggregation6.do_agg_union0(rs, c_view_map, start_pos, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
//				
//		end = System.nanoTime();
//		
//		aggregation_time = (end - start) * 1.0/second2nano;
//		
//		return citations;
//	}
	
//	public static ArrayList<HashSet<citation_view>> cal_covering_sets_schema_level(Query query, Connection c, PreparedStatement pst) throws SQLException
//	{
//	  
////	   System.out.println(max_author_num);
//	  
//	    Aggregation6.clear();
//	  
//		if(agg_intersection)
//		{
//			Aggregation6.cal_covering_set_schema_level_intersection(rs, c_view_map, Resultset_prefix_col_num, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
//			
//			return new ArrayList<HashSet<citation_view>>();
//			
//		}
//		else
//		{
//			ArrayList<HashSet<citation_view>> views_per_group = Aggregation6.cal_covering_set_schema_level_union(rs, c_view_map, Resultset_prefix_col_num, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
//			
//			return views_per_group;
//		}
//	}
	
//	public static ArrayList<HashSet<citation_view>> cal_covering_sets_subset_tuples(Query query, Vector<Head_strs> names, Connection c, PreparedStatement pst) throws SQLException
//    {
//	  
//	  Aggregation6.clear();
//	  
//	    tuple_id_list = Aggregation6.get_valid_tuple_ids(head_strs_rows_mapping, names);
//	    
//	  
//        if(agg_intersection)
//        {
//          Aggregation6.cal_covering_set_schema_level_intersection(rs, tuple_id_list, c_view_map, signiture_rid_mappings, Resultset_prefix_col_num, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
//            
//            return new ArrayList<HashSet<citation_view>>();
//            
//        }
//        else
//        {
//            ArrayList<HashSet<citation_view>> views_per_group = Aggregation6.cal_covering_set_schema_level_union(rs, c_view_map, Resultset_prefix_col_num, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
//            
//            return views_per_group;
//        }
//    }
	
	
	public static HashSet<String> gen_citation_schema_level(Connection c, PreparedStatement pst) throws JSONException, SQLException
	{
//		if(agg_intersection)
//		{
//			HashSet<String>	citations = Aggregation4.do_agg_intersection_min(covering_set_schema_level, valid_view_mapping_schema_level, rs, c_view_map, Resultset_prefix_col_num, query_lambda_str, author_mapping, max_author_num, query_ids, c, pst, true);
//			
//			return citations;
//		}
//		else
	  
	  HashSet<Tuple> valid_view_mappings = new HashSet<Tuple>();
      
      for(citation_view view_mapping: covering_set_schema_level.c_vec)
      {
        valid_view_mappings.add(view_mapping.get_view_tuple());
      }
      
      HashSet<Covering_set> covering_sets = new HashSet<Covering_set>();
      
      covering_sets.add(covering_set_schema_level);
		
      HashSet<String> citations = Aggregation6.do_agg_union(covering_sets, valid_view_mappings, signiture_view_mappings_mappings, signiture_rid_mappings, rs, signiture_rid_mappings, Resultset_prefix_col_num, author_mapping, max_author_num, lambda_term_id_mapping, c, pst, view_query_mapping, citation_queries, true);
			
      return citations;
	}
	
//	public static HashSet<String> gen_citation_subset_tuples(ArrayList<HashSet<citation_view>> views_per_group, Connection c, PreparedStatement pst) throws JSONException, SQLException
//    {
//        if(agg_intersection)
//        {
//            HashSet<String> citations = Aggregation6.do_agg_intersection_subset(rs, tuple_id_list, c_view_map, Resultset_prefix_col_num, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);//(rs, c_view_map, Resultset_prefix_col_num, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
//            
//            return citations;
//        }
//        else
//        {
//            HashSet<String> citations = Aggregation6.do_agg_union(views_per_group, rs, c_view_map, signiture_rid_mappings, Resultset_prefix_col_num, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
//            
//            return citations;
//        }
//    }
	
	public static HashSet<String> tuple_gen_agg_citations2(Query query) throws ClassNotFoundException, SQLException, JSONException
	{
		start = System.nanoTime();
		
		int start_pos = Resultset_prefix_col_num;
		
		Connection c = null;
		
	    PreparedStatement pst = null;
	      
		Class.forName("org.postgresql.Driver");
		
	    c = DriverManager
	        .getConnection(populate_db.db_url,
	    	        populate_db.usr_name,populate_db.passwd);
		
	    
	    //rs, c_view_map, start_pos, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst
		HashSet<String> citations = new HashSet<String>(); //Aggregation3.do_agg_intersection(rs, c_view_map, start_pos, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
		
		c.close();
		
		end = System.nanoTime();
		
		aggregation_time = (end - start) * 1.0/second2nano;
		
		return citations;
	}
	
	public static HashSet<String> tuple_gen_agg_citations(Query query, ArrayList<Head_strs> heads, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException, JSONException
	{
		int start_pos = query.head.args.size() + query.body.size();
		
		HashSet<String> citations = new HashSet<String>(); //Aggregation5.do_agg_intersection(rs, c_view_map, start_pos, heads, head_strs_rows_mapping, view_query_mapping, query_lambda_str, author_mapping, max_author_num, query_ids, view_list, c, pst, true);
				
		return citations;
	}
	
//	public static Vector<String> tuple_gen_agg_citations(Vector<Vector<citation_view_vector2>> c_views, Vector<Integer> ids) throws ClassNotFoundException, SQLException, JSONException
//	{
//		Vector<Vector<citation_view_vector2>> agg_res = Aggregation1.aggegate(c_views, ids);
//		
//		Vector<String> citation_aggs = new Vector<String>();
//		
//		for(int i = 0; i<agg_res.size(); i++)
//		{
//			
//			String str = gen_citation1.get_citation_agg(agg_res.get(i), max_author_num, view_query_mapping, query_lambda_str, author_mapping);
//			
//			citation_aggs.add(str);
////			
////			System.out.print(agg_res.get(i).get(0).toString() + ":");
////			
////			System.out.println(str);
//
//		}
//		
//		return citation_aggs;
//	}
	
	
//	public static void tuple_reasoning(Query query, Vector<Head_strs> head_vals, String f_name, HashMap<Head_strs, Vector<Vector<Covering_set>>> citation_view_map1, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
//	{
////		query = get_full_query(query);
////		
////		System.out.println(query);
//		
//		file_name = f_name;
//				
//		tuple_reasoning(query, c, pst);
//		
//		
//		
////		return citation_views;
//	}
	
	public static void reset() throws SQLException
	{
		valid_conditions.clear();
		
		valid_lambda_terms.clear();
		
		condition_id_mapping.clear();
		
		lambda_term_id_mapping.clear();
		
		conditions_map.clear();
		
		lambda_terms_map.clear();
		
//		tuple_mapping.clear();
										
		authors.clear();
		
		view_query_mapping = new HashMap<String, HashMap<String, String>>();
				
		author_mapping.clear();
		
		query_lambda_str.clear();
		
		c_view_map.clear();
		
		if(rs != null)
			rs.close();
		
		covering_set_num = 0;
		
		Resultset_prefix_col_num = 0;
		
		pre_processing_time = 0.0;
		
		query_time = 0.0;
		
		reasoning_time = 0.0;
		
		population_time = 0.0;
		
		aggregation_time = 0.0;
		
		max_author_num.put("Contributor", -1);
		
		max_author_num.put("info", -1);
		
		max_author_num.put("Investigator", -1);
		
		max_author_num.put("Program", -1);
		
		max_author_num.put("Funding", -1);
		
		view_tuple_mapping.clear();
		
		tuple_num = 0;
		
		citation_queries.clear();
		
		group_num = 0;
		
		q_subgoal_id.clear();
		
		query_ids = new IntList();
		
		signiture_rid_mappings.clear();
		
		relation_arg_mapping.clear();
		
		if(valid_view_mapping_schema_level != null)
		  valid_view_mapping_schema_level.clear();
		
		valid_view_mapping_schema_level = null;
	}
	
	public static String get_full_query(String query) throws ClassNotFoundException, SQLException
	{
		
		
		String head = query.split(":")[0];
		
		String []subgoals = query.split(":")[1].split(",");
		
		String subgoal_str = new String();
		
		boolean start = false;
		
		for(int i = 0; i<subgoals.length; i++)
		{
			if(subgoals[i].contains("()"))
			{
				if(start)
				{
					subgoal_str += ",";
				}
				
				subgoals[i] = subgoals[i].substring(0, subgoals[i].length() - 2).trim();
				
				subgoal_str += subgoals[i] + "(";
				
				Vector<String> args = Parse_datalog.get_columns(subgoals[i]);
				
				for(int k = 0; k<args.size(); k++)
				{
					if(k >= 1)
						subgoal_str += ",";
					
					subgoal_str += args.get(k);
				}
				
				subgoal_str += ")";
				
				start = true;
			}
			else
			{
				subgoal_str += "," + subgoals[i];
			}
		}
		
		String update_q = head + ":" + subgoal_str;
		
		return update_q;
	}
	
	static Vector<String> get_args(String table_name) throws SQLException, ClassNotFoundException
	{
		 Class.forName("org.postgresql.Driver");
         Connection c = DriverManager
            .getConnection(populate_db.db_url,
        	        populate_db.usr_name,populate_db.passwd);
         
//         pst = c.prepareStatement("SELECT *  FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'v2'");
         
         
         String get_arg_sql = "select arguments from subgoal_arguments where subgoal_names = '" + table_name + "'";
         
         PreparedStatement pst = c.prepareStatement(get_arg_sql);
         
         ResultSet rs = pst.executeQuery();
         
         
         Vector<String> args = new Vector<String>();
         
         while(rs.next())
         {
        	 args.add(rs.getString(1));
         }
         
         
         c.close();
         
         return args;
	}
	
	public static void gen_citation_all(Vector<Vector<Covering_set>> citation_views) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i<citation_views.size(); i++)
		{
			Vector<Covering_set> c_vec = citation_views.get(i);
			
			for(int j = 0; j< c_vec.size(); j++)
			{
				gen_citation(c_vec.get(j));
			}
		}
	}
	
	public static void output_sql(Vector<Vector<Covering_set>> citation_views) throws ClassNotFoundException, SQLException
	{
		for(int i = 0; i<citation_views.size(); i++)
		{
			Vector<Covering_set> c_vec = citation_views.get(i);
			
			for(int j = 0; j<c_vec.size(); j++)
			{
				if(j >= 1)
					System.out.print(",");
				System.out.print(c_vec.get(j).toString());
//				gen_citation(c_vec.get(j));
			}
			
			System.out.println();
		}
	}
	
//	public static void query_lambda_term_table(Vector<String> subgoal_names, Query query, Connection c, PreparedStatement pst) throws SQLException
//	{
//		String q = "select * from view2lambda_term";
//		
////		HashMap<String, String[]> lambda_term_table_map = new HashMap<String, String[]>();
//		
//		pst = c.prepareStatement(q);
//		
//		ResultSet rs = pst.executeQuery();
//					
//		while(rs.next())
//		{
//			String view_name = rs.getString(1);
//			
//			String lambda_term = rs.getString(2);
//			
//			String table_name = rs.getString(3);
//			
//			if(subgoal_names.contains(table_name))
//			{
//				HashSet<String> lambda_terms = return_vals.get(table_name); 
//				
//				if(lambda_terms==null)
//				{
//					lambda_terms = new HashSet<String>();
//					
//					lambda_terms.add(lambda_term);
//					
//					return_vals.put(table_name, lambda_terms);
//					
//					String [] com = new String[2];
//					
//					com[0] = table_name;
//					
//					com[1] = lambda_term;
//					
//					table_lambda_terms.add(com);
//					
//					
//					HashMap<String, Integer> l_seq = table_lambda_term_seq.get(table_name);
//					
//					if(l_seq == null)
//					{
//						l_seq = new HashMap<String, Integer>();
//						
//						l_seq.put(lambda_term, return_vals.get(table_name).size());
//						
//						table_lambda_term_seq.put(table_name, l_seq);
//						
//					}
//					else
//					{
//						l_seq.put(lambda_term, return_vals.get(table_name).size());
//					}
//					
//					
//					
//					HashMap<String, Integer> lambda_term_seq = view_lambda_term_map.get(view_name);
//					
//					if(lambda_term_seq == null)
//					{
//						lambda_term_seq = new HashMap<String, Integer>();
//						
//						lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
//						
//						view_lambda_term_map.put(view_name, lambda_term_seq);
//					}
//					else
//					{
//						lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
//					}
//					
//				}
//				else
//				{
//					if(!lambda_terms.contains(lambda_term))
//					{
//						lambda_terms.add(lambda_term);
//						
//						String [] com = new String[2];
//						
//						com[0] = table_name;
//						
//						com[1] = lambda_term;
//						
//						table_lambda_terms.add(com);
//						
//						HashMap<String, Integer> l_seq = table_lambda_term_seq.get(table_name);
//						
//						if(l_seq == null)
//						{
//							l_seq = new HashMap<String, Integer>();
//							
//							l_seq.put(lambda_term, return_vals.get(table_name).size());
//							
//							table_lambda_term_seq.put(table_name, l_seq);
//							
//						}
//						else
//						{
//							l_seq.put(lambda_term, return_vals.get(table_name).size());
//						}
//						
//						
//						
//						HashMap<String, Integer> lambda_term_seq = view_lambda_term_map.get(view_name);
//						
//						if(lambda_term_seq == null)
//						{
//							lambda_term_seq = new HashMap<String, Integer>();
//							
//							lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
//							
//							view_lambda_term_map.put(view_name, lambda_term_seq);
//						}
//						else
//						{
//							lambda_term_seq.put(lambda_term, return_vals.get(table_name).size());
//						}
//						
//					}
//					else
//					{
//						HashMap<String, Integer> lambda_term_seq = view_lambda_term_map.get(view_name);
//						
//						int seq = table_lambda_term_seq.get(table_name).get(lambda_term);
//						
//						if(lambda_term_seq == null)
//						{
//							lambda_term_seq = new HashMap<String, Integer>();
//							
//							lambda_term_seq.put(lambda_term, seq);
//							
//							view_lambda_term_map.put(view_name, lambda_term_seq);
//						}
//						else
//						{
//							lambda_term_seq.put(lambda_term, seq);
//						}
//					}
//				}
//				
//				
//							
//				
//			}
//			
////			lambda_term_table_map.put(rs.getString(1), table_lambda_terms);
//		}
//	}
//	
	
	static String get_args_web_view(String subgoal, Connection c, PreparedStatement pst) throws SQLException
	{
		String q = "select arguments from subgoal_arguments where subgoal_names ='" + subgoal + "'";
		
		pst = c.prepareStatement(q);
		
		ResultSet r = pst.executeQuery();
		
		String subgoal_args = new String();
		
		if(r.next())
		{
			subgoal_args += r.getString(1);
		}
		
		return subgoal_args;
		
	}
	
		
	static String get_subgoals (Vector<String> subgoal_names, Vector<String> subgoal_arguments, String view_id, Connection c, PreparedStatement pst ) throws SQLException
	{
		String subgoal_query = "select s.subgoal_names, a.arguments from view2subgoals s join subgoal_arguments a using (subgoal_names) where s.view = '" + view_id + "'";
		
		pst = c.prepareStatement(subgoal_query);
		
		ResultSet rs = pst.executeQuery();
				
		String subgoal_str = new String();
		
		int num = 0;
		
		while(rs.next())
		{
			if(num >= 1)
			{
				subgoal_str += ",";
			}
			
			subgoal_str += rs.getString(1) + "(";
			
			subgoal_str += rs.getString(2) + ")";
			
			num++;
		}
		
		return subgoal_str;
		
	}
	
	static HashSet<Tuple> check_distinguished_variables(HashSet<Tuple> viewtuples, Query q)
	{
		HashSet<Tuple> update_tuples = new HashSet<Tuple>();
		
		for(Iterator iter = viewtuples.iterator(); iter.hasNext();)
		{			
			Tuple tuple = (Tuple)iter.next();
			
//			System.out.println(tuple);
			
			if(check_head_mapping(tuple.getArgs(), q))// || check_condition_mapping(tuple.getArgs(), q))
			{
				update_tuples.add(tuple);
			}
			
		}
		
		return update_tuples;
	}
	
	static boolean check_head_mapping(Vector<Argument> args, Query q)
	{
		
		Vector<Argument> q_head_args = q.head.args;
		
		for(int i = 0; i<args.size(); i++)
		{
			for(int j = 0; j<q_head_args.size(); j++)
			{	
				if(args.get(i).toString().equals(q_head_args.get(j).toString()) && args.get(i).relation_name.equals(q_head_args.get(j).relation_name))
					return true;
			}
		}
		
		return false;
		
	}
	
	static boolean check_condition_mapping(Vector<Argument> args, Query q)
	{
		for(int i = 0; i<args.size(); i++)
		{
			for(int j = 0; j<q.conditions.size(); j++)
			{
				Conditions condition = q.conditions.get(j);
				
//				System.out.print(args.get(i) + ":::" + condition.subgoal1 + "_" + condition.arg1);
				
//				if(!condition.arg2.isConst())
//					System.out.print(condition.subgoal2 + "_" + condition.arg2);
//				
//				System.out.println();
				
				if(args.get(i).toString().equals(condition.subgoal1 + populate_db.separator + condition.arg1) || (condition.subgoal2 != null && condition.subgoal2.isEmpty() && args.get(i).toString().equals(condition.subgoal2 + populate_db.separator + condition.arg2)))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	static void build_view_tuple_mapping(Tuple tuple, Vector<Subgoal> subgoals)
	{
		for(Iterator iter = subgoals.iterator(); iter.hasNext();)
		{
			Subgoal subgoal = (Subgoal) iter.next();
			
			String key = tuple.name + populate_db.separator + subgoal.name;
			
			ArrayList<Tuple> curr_tuples = view_tuple_mapping.get(key);
			
			if(curr_tuples == null)
			{
				curr_tuples = new ArrayList<Tuple>();
				
				curr_tuples.add(tuple);
				
				view_tuple_mapping.put(key, curr_tuples);
			}
			else
			{
				curr_tuples.add(tuple);
				
				view_tuple_mapping.put(key, curr_tuples);
			}
			
		}
	}
	
	static void build_head_variable_mapping(Query query, Tuple tuple)
    {
        for(int i = 0; i<tuple.getArgs().size(); i++)
        {
                        
            Argument target_attr_name = (Argument) tuple.getArgs().get(i);
            
            int target_arg_id = query.head.args.indexOf(target_attr_name);
            
            if(target_arg_id < 0)
              continue;
            
            ArrayList<Tuple> tuples = head_variable_view_mapping[target_arg_id];
            
            String relation_name = target_attr_name.relation_name;
            
            if(relation_arg_mapping.get(relation_name) == null)
            {
                HashSet<Integer> args = new HashSet<Integer>();
                
                args.add(target_arg_id);
                
                relation_arg_mapping.put(relation_name, args);
            }
            else
            {
                relation_arg_mapping.get(relation_name).add(target_arg_id);
            }   
            
            if(tuples == null)
            {
                tuples = new ArrayList<Tuple>();
                
                tuples.add(tuple);
            }
            else
                tuples.add(tuple);
            
            head_variable_view_mapping[target_arg_id] = tuples;
        }
    }
	
//	static void build_head_variable_mapping(Query view, Tuple tuple)
//	{
//		for(int i = 0; i<view.head.args.size(); i++)
//		{
//			
//			Argument source_attr_name = (Argument) view.head.args.get(i);
//			
//			Argument target_attr_name = (Argument)tuple.phi.apply(source_attr_name);
//			
//			ArrayList<Tuple> tuples = head_variable_view_mapping.get(target_attr_name);
//			
//			if(tuples == null)
//			{
//				tuples = new ArrayList<Tuple>();
//				
//				tuples.add(tuple);
//			}
//			else
//				tuples.add(tuple);
//			
//			head_variable_view_mapping.put(target_attr_name, tuples);
//		}
//	}
	
	static void build_query_head_variable_mapping(Query query)
	{
		for(int i = 0; i<query.head.args.size(); i++)
		{
			Argument head_arg = (Argument)query.head.args.get(i);
			
			String head_arg_str = head_arg.toString();
			
			int partition_id = head_arg_str.indexOf(populate_db.separator);
			
			String relation_name = head_arg_str.substring(0, partition_id);
			
			ArrayList<Integer> head_variable_strs = head_variable_query_mapping.get(relation_name);
			
			if(head_variable_strs == null)
			{
				head_variable_strs = new ArrayList<Integer>();
			}
			
			head_variable_strs.add(i);
			
			head_variable_query_mapping.put(relation_name, head_variable_strs);
		}
	}
	
	static void build_subgoal_id_mapping(Query q)
	{
		for(int i = 0; i<q.body.size(); i++)
		{
			Subgoal subgoal = (Subgoal) q.body.get(i);
			
			q_subgoal_id.put(subgoal.name, i);
		}
	}

//	   public static HashSet pre_processing(Vector<Query> views, Query q, Vector<String> partial_mapping_strings, HashMap<String, HashSet<Tuple>> partial_mapping_view_mapping_mappings, HashMap<String, Integer> with_sub_queries_id_mappings, Vector<String> full_mapping_condition_str, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
//	    {
//	        
//	        build_query_head_variable_mapping(q);
//	        
////	        for(int k = 0; k<views.size(); k++)
////	        {
////	            view_list.add(views.get(k).name);
////	        }
//
//	        HashSet<Conditions> query_negated_conditions = q.get_all_negated_conditions();
//
//	        
////	      q = q.minimize();
//
//	        // construct the canonical db
//	        Database canDb = CoreCover.constructCanonicalDB(q);
//	        UserLib.myprintln("canDb = " + canDb.toString());
//
//	        // compute view tuples
//	        viewTuples = CoreCover.computeViewTuples(canDb, views);
//	        
//	        viewTuples = check_distinguished_variables(viewTuples, q);
//	                        
//	        head_variable_view_mapping = new ArrayList[q.head.args.size()];
//	        
//	        for(Iterator iter = viewTuples.iterator();iter.hasNext();)
//	        {
//	            Tuple tuple = (Tuple)iter.next();
//	            
//	            build_view_tuple_mapping(tuple, q.body);
//	            
////	          System.out.println(tuple.name + "|" + tuple.mapSubgoals_str);
//	            
//	            for(int i = 0; i<tuple.conditions.size(); i++)
//	            {
//	                Conditions condition = tuple.conditions.get(i);
//	                
//	                if(condition.get_mapping1 == true && condition.get_mapping2 == true)
//	                {
//	                  
//	                  if(query_negated_conditions.contains(condition))
//	                  {
//	                    iter.remove();
//	                    
//	                    continue;
//	                  }
//	                  
//	                  if(condition.arg2.isConst())
//	                    continue;
//	                  
//	                  if(!q.conditions.contains(condition) && !valid_conditions.contains(condition))
//	                  {
//	                    valid_conditions.add(tuple.conditions.get(i));
//	                    
//	                    String curr_full_mapping_condition_str = Query_converter.get_full_mapping_condition_str(condition);
//	                    
//	                    full_mapping_condition_str.add(curr_full_mapping_condition_str);
//	                    
//	                    ArrayList<Tuple> curr_tuples = conditions_map.get(condition);
//	                    
//	                    if(curr_tuples == null)
//	                    {
//	                        curr_tuples =  new ArrayList<Tuple>();
//	                        
//	                        curr_tuples.add(tuple);
//	                        
//	                        conditions_map.put(condition, curr_tuples);
//	                    }
//	                    else
//	                    {                   
//	                        curr_tuples.add(tuple);
//	                        
//	                        conditions_map.put(condition, curr_tuples);
//	                    }
//	                    
//	                  }
//	                  else
//	                  {
//	                    if(valid_conditions.contains(condition))
//	                    {
////	                      valid_conditions.add(tuple.conditions.get(i));
//	                      
////	                      String curr_full_mapping_condition_str = Query_converter.get_full_mapping_condition_str(condition);
//	                      
////	                      full_mapping_condition_str.add(curr_full_mapping_condition_str);
//	                      
//	                      ArrayList<Tuple> curr_tuples = conditions_map.get(condition);
//	                      
//	                      if(curr_tuples == null)
//	                      {
//	                          curr_tuples =  new ArrayList<Tuple>();
//	                          
//	                          curr_tuples.add(tuple);
//	                          
//	                          conditions_map.put(condition, curr_tuples);
//	                      }
//	                      else
//	                      {                   
//	                          curr_tuples.add(tuple);
//	                          
//	                          conditions_map.put(condition, curr_tuples);
//	                      }
//	                      
//	                    }
//	                  }
//	                  
//	                  
//	                  
//	                  
//	                }
//	                
////	                ArrayList<Tuple> curr_tuples = conditions_map.get(condition);
////	                
////	                if(curr_tuples == null)
////	                {
////	                    curr_tuples =  new ArrayList<Tuple>();
////	                    
////	                    curr_tuples.add(tuple);
////	                    
////	                    conditions_map.put(condition, curr_tuples);
////	                }
////	                else
////	                {                   
////	                    curr_tuples.add(tuple);
////	                    
////	                    conditions_map.put(condition, curr_tuples);
////	                }
////	                                
////	              if(condition_id_mapping.get(condition) == null)
////	              {
////	                  condition_id_mapping.put(condition, valid_conditions.size());
////	                  
////	                  valid_conditions.add(tuple.conditions.get(i));
////	              }
//	            }
//	            
//	            Vector<String> curr_partial_mapping_expression = Query_converter.get_partial_mapping_boolean_expressions(tuple, tuple.query, with_sub_queries_id_mappings);
//	            
//	            if(!curr_partial_mapping_expression.isEmpty())
//	            {
//	              for(String curr_partial_mapping_string : curr_partial_mapping_expression)
//	              {
//	                if(!partial_mapping_strings.contains(curr_partial_mapping_string))
//	                {
//	                  partial_mapping_strings.add(curr_partial_mapping_string);
//	                  
//	                  HashSet<Tuple> curr_view_mappings = new HashSet<Tuple>();
//	                  
//	                  curr_view_mappings.add(tuple);
//	                  
//	                  partial_mapping_view_mapping_mappings.put(curr_partial_mapping_string, curr_view_mappings);
//	                  
//	                }
//	                else
//	                {
//	                  partial_mapping_view_mapping_mappings.get(curr_partial_mapping_string).add(tuple);
//	                }
//	                
//	              }
//	              
//	              
//	              
//	            }
//	            
//	            build_head_variable_mapping(q, tuple);
//	            
////	            if(tuple_mapping.get(tuple.name) == null)
////	            {
////	                ArrayList<Tuple> tuples = new ArrayList<Tuple>();
////	                
////	                tuples.add(tuple);
////	                
////	                tuple_mapping.put(tuple.name, tuples);
////	            }
////	            else
////	            {
////	                ArrayList<Tuple> tuples = tuple_mapping.get(tuple.name);
////	                
////	                tuples.add(tuple);
////	                
////	                tuple_mapping.put(tuple.name, tuples);
////	            }
//	            
//	            
//	            
//	            
//	            
//	            for(int i = 0; i<tuple.lambda_terms.size(); i++)
//	            {
//	                String lambda_str = tuple.lambda_terms.get(i).toString();
//	                                
//	                if(lambda_term_id_mapping.get(lambda_str) == null)
//	                {
//	                    lambda_term_id_mapping.put(lambda_str, valid_lambda_terms.size());
//	                    
//	                    valid_lambda_terms.add(tuple.lambda_terms.get(i));
//	                }
//	            }
//	            
//	        }
//
//	        if(prepare_info)
//	            prepare_citation_information(views, c, pst);
//	                
//	        
////	      System.out.println("partial_mapping_conditions:::" + partial_mapping_strings);
//	        
//	        return viewTuples;
//	        
//	        
//	    }
//	
//	public static HashSet<Tuple> pre_processing(Vector<Query> views, Query q, Vector<String> partial_mapping_strings, HashMap<String, HashSet<Tuple>> partial_mapping_view_mapping_mappings, HashMap<String, Integer> with_sub_queries_id_mappings, Vector<String> full_mapping_condition_str, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
//	{
//	
//		build_query_head_variable_mapping(q);
//				
//		for(int k = 0; k<views.size(); k++)
//		{
//			view_list.add(views.get(k).name);
//			
//			view_mapping.put(views.get(k).name, views.get(k));
//		}
//
//		build_subgoal_id_mapping(q);
//		
////	    q = q.minimize();
//
//	    // construct the canonical db
//	    Database canDb = CoreCover.constructCanonicalDB(q);
//	    UserLib.myprintln("canDb = " + canDb.toString());
//
//	    // compute view tuples
//	    viewTuples = CoreCover.computeViewTuples(canDb, views);
//	    
//	    viewTuples = check_distinguished_variables(viewTuples, q);
//	    	    	    
//	    unique_tuple_num = viewTuples.size();
//	    
//	    for(Iterator iter = viewTuples.iterator();iter.hasNext();)
//	    {
//	    	Tuple tuple = (Tuple)iter.next();
//	    	
////	    	System.out.println(tuple);
//	    	
//	    	HashSet<Subgoal> mapped_relations = tuple.getTargetSubgoals();
//	    	
//	    	build_view_tuple_mapping(tuple, mapped_relations);
//	    	
//	    	
//	    	build_head_variable_mapping(tuple.query, tuple);
//	    	if(tuple_mapping.get(tuple.name) == null)
//	    	{
//	    		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
//	    		
//	    		tuples.add(tuple);
//	    		
//	    		tuple_mapping.put(tuple.name, tuples);
//	    	}
//	    	else
//	    	{
//	    		ArrayList<Tuple> tuples = tuple_mapping.get(tuple.name);
//	    		
//	    		tuples.add(tuple);
//	    		
//	    		tuple_mapping.put(tuple.name, tuples);
//	    	}
//	    	
//	    	
//	    	for(int i = 0; i<tuple.conditions.size(); i++)
//	    	{
//	    		
//	    		if(tuple.conditions.get(i).arg2.isConst())
//	    			continue;
//	    		
//	    		String condition_str = tuple.conditions.get(i).toString();
//	    		
//	    		ArrayList<Tuple> curr_tuples = conditions_map.get(condition_str);
//	    		
//	    		if(curr_tuples == null)
//	    		{
//	    			curr_tuples =  new ArrayList<Tuple>();
//	    			
//	    			curr_tuples.add(tuple);
//	    			
//	    			conditions_map.put(condition_str, curr_tuples);
//	    		}
//	    		else
//	    		{	    			
//	    			curr_tuples.add(tuple);
//	    			
//	    			conditions_map.put(condition_str, curr_tuples);
//	    		}
//	    			    		
////	    		if(condition_id_mapping.get(condition_str) == null)
//	    		{
////	    			condition_id_mapping.put(condition_str, valid_conditions.size());
//	    			
//	    			valid_conditions.add(tuple.conditions.get(i));
//	    		}
//	    	}
//	    	
//	    	for(int i = 0; i<tuple.lambda_terms.size(); i++)
//	    	{
//	    		String lambda_str = tuple.lambda_terms.get(i).toString();
//	    			    		
//	    		if(lambda_term_id_mapping.get(lambda_str) == null)
//	    		{
//	    			lambda_term_id_mapping.put(lambda_str, valid_lambda_terms.size());
//	    			
//	    			valid_lambda_terms.add(tuple.lambda_terms.get(i));
//	    		}
//	    	}
//	    	
//	    }
//
//	    // compute tuple-cores
//	    
//	    if(prepare_info)
//	    	prepare_citation_information(c, pst);
//	    	    
//	    return viewTuples;
//	    
//	    
//	}
	
//	public static void prepare_citation_information(Vector<Query> views, Connection c, PreparedStatement pst) throws SQLException
//	{
//		gen_citation0.get_all_query_ids(query_ids, c, pst);
//	    
////	    view_query_mapping = new ArrayList<HashMap<String, Integer>>(views.size());
//	    		
//		for(int i = 0; i<query_ids.size; i++)
//		{
//			query_lambda_str.add(null);
//		}
//	    	    
//	    gen_citation0.init_author_mapping(views, author_mapping, max_author_num, c, pst, citation_queries, view_query_mapping);
//
//	}
	
	public static HashMap<String, String> get_citation_queries(String view_name)
	{
//		int view_id = view_list.find(view_name);
		
		return view_query_mapping.get(view_name);
	}
		
	public static void tuple_reasoning(Query q, Vector<Query> views, Vector<Query> cqs, HashMap<String, HashMap<String, String>> view_citation_query_mappings, Connection c, PreparedStatement pst) throws ClassNotFoundException, SQLException, IOException, InterruptedException, JSONException
	{
		
////		Query q = Parse_datalog.parse_query(query, valid_subgoal_id);
//		
////		Query alt_q = gen_alternative_query(q, valid_subgoal_id);
//				
//		reset();
//		
////		Vector<Query> views = populate_db.get_views_schema(c, pst);
//		
//		Vector<Query> views = Load_views_and_citation_queries.get_views(populate_db.synthetic_view_files, c, pst);
//		
//		start = System.nanoTime();
//		
//		Vector<String> partial_mapping_strings = new Vector<String>();
//        
//        HashMap<String, HashSet<Tuple>> partial_mapping_view_mapping_mappings = new HashMap<String, HashSet<Tuple>>();
//        
//        
//        HashMap<String, Integer> with_sub_queries_id_mappings = new HashMap<String, Integer>();
//        
//        
//        Vector<String> full_mapping_condition_str = new Vector<String>();
//        
//        HashSet<Tuple> view_tuples = pre_processing(views, q, partial_mapping_strings, partial_mapping_view_mapping_mappings, with_sub_queries_id_mappings, full_mapping_condition_str, c,pst);
//        
//        String sql = null;
//        
//        if(!test_case)
//            sql = Query_converter.datalog2sql_citation_agg(true, q, partial_mapping_strings, partial_mapping_view_mapping_mappings, with_sub_queries_id_mappings, full_mapping_condition_str, valid_conditions, valid_lambda_terms, view_tuples);
//        else
//            sql = Query_converter.datalog2sql_citation_test3(q, partial_mapping_strings, partial_mapping_view_mapping_mappings, with_sub_queries_id_mappings, full_mapping_condition_str, valid_conditions, valid_lambda_terms, view_tuples);
//        
//        reasoning(q, partial_mapping_strings, partial_mapping_view_mapping_mappings, c, pst, sql, view_tuples);

	  
	  reset();
      
//      Vector<Query> views = Load_views_and_citation_queries.get_views(populate_db.synthetic_view_files, c, pst);
      
      start = System.nanoTime();
      
      Vector<String> partial_mapping_strings = new Vector<String>();
      
      HashMap<String, HashSet<Tuple>> partial_mapping_view_mapping_mappings = new HashMap<String, HashSet<Tuple>>();
      
      
      HashMap<String, Integer> with_sub_queries_id_mappings = new HashMap<String, Integer>();
      
      
      if(!q.head.has_agg)
      {
        head_variable_view_mapping = new ArrayList[q.head.args.size()];
      }
      else
      {
        head_variable_view_mapping = new ArrayList[q.head.agg_args.size()];
      }
      
      Vector<String> full_mapping_condition_str = new Vector<String>();
      
//    HashSet<Tuple> view_tuples = pre_processing(views, q, partial_mapping_strings, partial_mapping_view_mapping_mappings, with_sub_queries_id_mappings, full_mapping_condition_str, c,pst);
      
      viewTuples = Schema_reasoning_with_agg.pre_processing(true, citation_queries, max_author_num, view_query_mapping, author_mapping, query_ids, query_lambda_str, prepare_info, lambda_term_id_mapping, valid_lambda_terms, conditions_map, valid_conditions, view_tuple_mapping, head_variable_query_mapping, relation_arg_mapping, head_variable_view_mapping, views, cqs, view_citation_query_mappings, q, partial_mapping_strings, partial_mapping_view_mapping_mappings, with_sub_queries_id_mappings, full_mapping_condition_str, q_subgoal_id, c,pst);
      
      String sql = null;
      
      if(!test_case)
          sql = Query_converter.datalog2sql_citation_agg(true, q, partial_mapping_strings, partial_mapping_view_mapping_mappings, with_sub_queries_id_mappings, full_mapping_condition_str, valid_conditions, valid_lambda_terms, viewTuples);
      else
          sql = Query_converter.datalog2sql_citation_agg_test(true, q, partial_mapping_strings, partial_mapping_view_mapping_mappings, with_sub_queries_id_mappings, full_mapping_condition_str, valid_conditions, valid_lambda_terms, viewTuples);
      
//      System.out.println(sql);
//      
//      System.out.println(q);
      
      reasoning_min.init_computation(view_mapping_id_mappings, view_mapping_cost, viewTuples, q, single_subgoal_relations, single_view_head_args);
      
      reasoning(q, partial_mapping_strings, partial_mapping_view_mapping_mappings, c, pst, sql, viewTuples);
		
//		HashSet<Tuple> viewTuples = pre_processing(views, q ,c,pst);
//		
//		get_citation_views(q, viewTuples, c, pst);
				
		
		
//		return citation_views;
		
	}
	
	public static void output (Vector<Vector<Vector<citation_view>>> citation_views)
	{
		for(int i = 0; i<citation_views.size(); i++)
		{
			Vector<Vector<citation_view>> citation_view_vec = citation_views.get(i);
			
			for(int j = 0; j<citation_view_vec.size();j++)
			{
				Vector<citation_view> c_view = citation_view_vec.get(j);
				
				if(j > 0)
					System.out.print(",");
				
				for(int k = 0; k<c_view.size();k++)
				{
					citation_view  cv = c_view.get(k);
					if(k > 0)
						System.out.print("*");
					System.out.print(cv);
				}
				
				
			}
			
			System.out.println();
			
		}
	}
	
	public static Vector<citation_view> gen_citation_arr(Vector<Query> views)
	{
		return new Vector<citation_view>(views.size());
	}
	
	public static void gen_citation(Covering_set citation_views) throws ClassNotFoundException, SQLException
	{
		for(citation_view view_mapping: citation_views.c_vec)
		{
			String query = view_mapping.get_full_query();
			
			System.out.println(query);
		}
	}
	
//	public static void get_citation_views(Query query, HashSet<Tuple> viewTuples, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, IOException, InterruptedException, JSONException
//	{
//		query_execution(query, c, pst, viewTuples);
//				
//	}
	
	
	public static String gen_conditions_query (Vector<Conditions> conditions)
	{
		String where = new String();
		
//		boolean start = false;
		
		for(int i = 0; i<conditions.size(); i++)
		{
//			if(!start)
//			{
//				where += " where";
//				start = true;
//			}
			
			if(i >= 1)
				where +=" and ";
			
			Conditions condition = conditions.get(i);
			
			if(condition.arg2.type != 1)
				where += condition.subgoal1 + "." + condition.arg1.origin_name + condition.op + condition.subgoal2 + "." + condition.arg2.origin_name;
			else
				where += condition.subgoal1 + "." + condition.arg1.origin_name + condition.op + condition.arg2;
		}
		
		return where;
	}
	
//	public static Vector<Conditions> remove_conflict_duplicate(Query query)
//	{
//		if(conditions.size() == 0 || query.conditions == null || query.conditions.size() == 0)
//		{
//			return conditions;
//		}
//		else
//		{
//
//			Vector<Conditions> update_conditions = new Vector<Conditions>();
//			
//			boolean remove = false;
//			
//			for(int i = 0; i<conditions.size(); i++)
//			{
//				remove = false;
//				
//				for(int j = 0; j<query.conditions.size(); j++)
//				{
//					if(Conditions.compare(query.conditions.get(j), conditions.get(i)) || Conditions.compare(query.conditions.get(j), Conditions.negation(conditions.get(i))))
//					{
//						remove = true;
//						break;
//					}
//				}
//				
//				if(!remove)
//					update_conditions.add(conditions.get(i));
//			}
//			
//			conditions = update_conditions;
//			
//			return conditions;
//			
//		}
//	}
	
//	public static HashSet<Conditions> remove_conflict_duplicate_view_mapping(Query query)
//	{
//		if(valid_conditions.size() == 0 || query.conditions == null || query.conditions.size() == 0)
//		{
//			
////			int id = 0;
////			
////			for(Iterator iter = valid_conditions.iterator(); iter.hasNext();)
////			{
////				Conditions condition = (Conditions) iter.next();
////				
////				condition_id_mapping.put(condition.toString(), id);
////				
////				id ++;
////			}
//			
//			return valid_conditions;
//		}
//		else
//		{
//
//			HashSet<Conditions> update_conditions = new HashSet<Conditions>();
//			
//			boolean remove = false;
//			
//			for(Iterator iter = valid_conditions.iterator(); iter.hasNext(); )
//			{
//				remove = false;
//				
//				Conditions v_condition = (Conditions) iter.next();
//				
//				for(int j = 0; j<query.conditions.size(); j++)
//				{
//					if(Conditions.compare(query.conditions.get(j), v_condition) || Conditions.compare(query.conditions.get(j), Conditions.negation(v_condition)))
//					{
//						remove = true;
//												
//						break;
//					}
//				}
//				
//				if(!remove)
//					update_conditions.add(v_condition);
//			}
//			
//			valid_conditions = update_conditions;
//			
////			int id = 0;
////			
////			for(Iterator iter = valid_conditions.iterator(); iter.hasNext();)
////			{
////				Conditions condition = (Conditions) iter.next();
////				
////				condition_id_mapping.put(condition.toString(), id);
////				
////				id ++;
////			}
//			
//			return valid_conditions;
//			
//		}
//	}
//	
//	public static void query_execution(Query query, Connection c, PreparedStatement pst, HashSet<Tuple> viewTuples) throws SQLException, ClassNotFoundException, IOException, InterruptedException, JSONException
//	{
//				
////		Vector<Vector<citation_view_vector2>> c_views = new Vector<Vector<citation_view_vector2>>();
//								
////		Query_converter.post_processing(query);
//		
////		Query_converter.pre_processing(query);
//		
//		remove_conflict_duplicate_view_mapping(query);
//					
//		String sql = null;
//		
//		if(!test_case)
//			sql = Query_converter.datalog2sql_citation3(query, viewTuples, valid_lambda_terms, valid_conditions, c, pst);
//		else
//			sql = Query_converter.datalog2sql_citation_test3(query, valid_lambda_terms, valid_conditions, viewTuples);
//								
//		end = System.nanoTime();
//		
//		pre_processing_time = (end - start) * 1.0/second2nano;
//		
//		reasoning(query, c, pst, sql, viewTuples);
//		
////		sql = sql.substring(0, sql.indexOf("where"));
//		
////		return c_views;
//	}
	
//	public static void initial_conditions_all(Query query, Vector<HashMap<String,boolean[]>> conditions_all)
//	{
//		
//		
//		
//		for(int i = 0; i < query.body.size(); i++)
//		{
//			HashMap<String, boolean[]> conditions = new HashMap<String, boolean[]>();
//			
//			Set set = citation_condition_id_map.keySet();
//			
//			for(Iterator iter = set.iterator(); iter.hasNext();)
//			{
//				String c_name = (String)iter.next();
//				
//				HashMap<String, Integer> citation_map = citation_condition_id_map.get(c_name);
//				
//				int size = citation_map.size();
//				
//				boolean[] b_values = new boolean[size];
//				
////				for(int k = 0; k<size; k++)
////				{
////					b_values.add(false);
////				}
//				
//				conditions.put(c_name, b_values);
//			}
//			
//			
//			conditions_all.add(conditions);
//		}
//	}
//	
	
//	static HashMap<String, ArrayList<Tuple>> get_valid_view_mapping(int pos1) throws SQLException
//	{
//		HashMap<String, ArrayList<Tuple>> curr_tuple_mapping = new HashMap<String, ArrayList<Tuple>>();
//		
//		int i = pos1;
//		
//		curr_tuple_mapping.putAll(tuple_mapping);
//		
//		if(valid_conditions.isEmpty())
//		{
//			
//			
//			return curr_tuple_mapping;
//		}
//		
//		for(Iterator iter = valid_conditions.iterator(); iter.hasNext();)
//		{
//			boolean condition = rs.getBoolean(i + 1);
//			
//			Conditions curr_condition = (Conditions) iter.next();
//			
//			if(!condition)
//			{							
//				String condition_str = curr_condition.toString();
//				
//				ArrayList<Tuple> invalid_views = conditions_map.get(condition_str);
//				
//				for(int k = 0; k<invalid_views.size(); k++)
//				{
//					Tuple tuple = invalid_views.get(k);
//					
//					ArrayList<Tuple> curr_tuples = curr_tuple_mapping.get(tuple.name);//new ArrayList<Tuple>();
//					
//					if(curr_tuples == null)
//					{
//						curr_tuples = new ArrayList<Tuple>();
//						
//						curr_tuples.addAll(tuple_mapping.get(tuple.name));
//					}
//					
//					
//					curr_tuples.remove(tuple);
//					
//					curr_tuple_mapping.put(tuple.name, curr_tuples);
//				}							
//			}
//			i++;
//		}	
//		
//		return curr_tuple_mapping;
//	}
//	
	
	public static void reasoning(Query query, Vector<String> partial_mapping_strings, HashMap<String, HashSet<Tuple>> partial_mapping_view_mapping_mappings, Connection c, PreparedStatement pst, String sql, HashSet<Tuple> viewTuples) throws SQLException, IOException, InterruptedException, JSONException
	{
		start = System.nanoTime();
		
		pst = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
//		System.out.println(sql);
				
		rs = pst.executeQuery();

		end = System.nanoTime();
		
		query_time = (end - start) * 1.0/second2nano;
		
		int lambda_term_num = valid_lambda_terms.size();

		String old_value = new String();
		
		Vector<Vector<String>> values = new Vector<Vector<String>>(); 
		
//		HashMap<Head_strs, Vector<String> > citation_strs = new HashMap<Head_strs, Vector<String> >();

		HashMap<String, HashSet<String>> citation_strings = new HashMap<String, HashSet<String>>();				
		
		
		HashSet<Covering_set> c_view_template = null;
		
		int start_pos = 0;
		
		int end_pos = -1;
		
		
		
		{		
			while(rs.next())
			{				
				tuple_num ++;
				
				String curr_str = new String();
								
				Vector<String> vals = new Vector<String>();
				
				for(int i = 0; i<query.head.args.size(); i++)
				{
					vals.add(rs.getString(i+1));
				}
				
				Head_strs h_vals = new Head_strs(vals);
				
				int pos1 = query.body.size() + query.head.args.size() + lambda_term_num;
				
				for(int i = pos1; i< pos1 + valid_conditions.size(); i++)
				{
					boolean condition = rs.getBoolean(i + 1);
					
					if(!condition)
					{
				
						curr_str += "0";
					}
					else
					{					
						
						curr_str += "1";
					}
				}
				
                for(int i = pos1 + valid_conditions.size(); i<pos1 + valid_conditions.size() + partial_mapping_strings.size(); i++)
                {
                  boolean condition = rs.getBoolean(i + 1);
                  
                  if(!condition)
                  {
              
                      curr_str += "0";
                  }
                  else
                  {                   
                      
                      curr_str += "1";
                  }
                }
				
				ArrayList<HashSet<String>> c_units = new ArrayList<HashSet<String>>();
				for(int i = query.head.args.size(); i<query.body.size() + query.head.args.size();i++)
				{
					String citation_vec = rs.getString(i + 1);
					
					curr_str += "####" + citation_vec + "####";
					
					if(citation_vec == null)
                    {
//                    System.out.println("citation_vec::" + citation_vec);
                      
                      continue;
                    }
					
//					System.out.println("citation_vec::" + citation_vec);
					
//					String[] citation_vec_arr = (String[]) citation_vec.getArray();
//					
//					for(String c_v: citation_vec_arr)
					{
					  String[] c_v_arrs = citation_vec.split("\\" + populate_db.separator);
					  
					  HashSet<String> c_unit_set = new HashSet<String>(Arrays.asList(c_v_arrs));
					  
					  c_units.add(c_unit_set);
					}
					
				}
				
				
				if(signiture_rid_mappings.get(curr_str) == null)
				{
				  HashSet<Integer> rids = new HashSet<Integer>();
				  
				  rids.add(tuple_num);
				  
				  signiture_rid_mappings.put(curr_str, rids);
				  
				  start = System.nanoTime();
                  
                  group_num ++;
                  
                  c_view_template = new HashSet<Covering_set>();
                                      
//                  HashMap<String, ArrayList<Tuple>> curr_tuple_mapping = get_valid_view_mapping(pos1);//(HashMap<String, ArrayList<Tuple>>) tuple_mapping.clone();
                  HashSet<Tuple> curr_views = (HashSet<Tuple>) viewTuples.clone();
                  
                  int i = pos1;
                  
                  for(Iterator iter = valid_conditions.iterator(); iter.hasNext();)
                  {
                      boolean condition = rs.getBoolean(i + 1);
                      
                      Conditions curr_condition = (Conditions) iter.next();
                      
                      if(!condition)
                      {                           
                          ArrayList<Tuple> invalid_views = conditions_map.get(curr_condition);
                          
                          curr_views.removeAll(invalid_views);
                      }
                      i++;
                  } 
                  
                  for(String partial_mapping_string: partial_mapping_strings)
                  {
                    boolean condition = rs.getBoolean(i + 1);
                    
                    if(!condition)
                    {                           
                        HashSet<Tuple> invalid_views = partial_mapping_view_mapping_mappings.get(partial_mapping_string);
                        
                        curr_views.removeAll(invalid_views);
                    }
                    i++;
                  }
                  
                  
                  
                  Resultset_prefix_col_num = query.body.size() + query.head.args.size();
                  
                  HashSet<Argument> all_head_vars = new HashSet<Argument>();
                  
                  HashSet<String> all_covered_subgoal_names = new HashSet<String>();
                  
                  HashSet<Tuple> c_unit_vec = get_citation_units_condition(c_units, query.body.size() + query.head.args.size(), query, all_head_vars, all_covered_subgoal_names, curr_views);
                  
                  HashSet<Tuple> selected_view_mappings = reasoning_min.greedy_algorithm(view_mapping_cost, c_unit_vec, all_covered_subgoal_names, all_head_vars, single_subgoal_relations, single_view_head_args);
                  
                  Covering_set covering_set = reasoning_min.get_valid_citation_combination(selected_view_mappings);
                  
                  double start = System.nanoTime();
                  
//                  if(isclustering)
//                    Covering_sets_clustering_by_relation.reasoning_single_tuple(head_variable_query_mapping, head_variable_view_mapping, relation_arg_mapping, c_unit_vec, query, rs, c_view_template, all_head_vars);
//                  else
//                    Covering_sets_no_clustering.reasoning_single_tuple(head_variable_query_mapping, head_variable_view_mapping, relation_arg_mapping, c_unit_vec, query, rs, c_view_template, all_head_vars);

//                  if(isclustering)
//                    get_valid_citation_combination(c_view_template, c_unit_vec,query, all_head_vars);
//                  else
//                    get_valid_citation_combination_no_clustering(c_view_template, c_unit_vec,query, all_head_vars);
                  
                  if(agg_intersection)
                  {
                    if(valid_view_mapping_schema_level == null)
                    {
                      valid_view_mapping_schema_level = new HashSet<Tuple>();
                      valid_view_mapping_schema_level.addAll(c_unit_vec);
                    }
                    else
                    {
                      valid_view_mapping_schema_level.retainAll(c_unit_vec);
                    }
                  }
                  else
                  {
                    if(valid_view_mapping_schema_level == null)
                    {
                      valid_view_mapping_schema_level = new HashSet<Tuple>();
                      
                      valid_view_mapping_schema_level.addAll(c_unit_vec);
                    }
                    else
                    {
                      valid_view_mapping_schema_level.addAll(c_unit_vec);
                    }
                  }
                  
                  signiture_view_mappings_mappings.put(curr_str, c_unit_vec);
                  
                  c_view_map.put(curr_str, covering_set);
                  
                  double end = System.nanoTime();
                  
                  covering_set_time = (end - start)*1.0/second2nano;
                  
                  covering_set_num += c_view_template.size();
                  
                  old_value = curr_str;
                  
                  if(head_strs_rows_mapping.get(h_vals) == null)
                  {
                      ArrayList<Integer> int_list = new ArrayList<Integer>();
                      
                      int_list.add(tuple_num);
                      
                      head_strs_rows_mapping.put(h_vals, int_list);
                  }
                  else
                  {
                      ArrayList<Integer> int_list = head_strs_rows_mapping.get(h_vals);
                      
                      int_list.add(tuple_num);
                      
                      head_strs_rows_mapping.put(h_vals, int_list);
                  }
                  
                  c_units.clear();

                  end = System.nanoTime();
                  
                  reasoning_time += (end - start) * 1.0/second2nano;
				  
				}
				else
				{
				  signiture_rid_mappings.get(curr_str).add(tuple_num);
				  
				  
                  start = System.nanoTime();
                  
                  covering_set_num += c_view_template.size();
                  
                  if(head_strs_rows_mapping.get(h_vals) == null)
                  {
                      ArrayList<Integer> int_list = new ArrayList<Integer>();
                      
                      int_list.add(tuple_num);
                      
                      head_strs_rows_mapping.put(h_vals, int_list);
                  }
                  else
                  {
                      ArrayList<Integer> int_list = head_strs_rows_mapping.get(h_vals);
                      
                      int_list.add(tuple_num);
                      
                      head_strs_rows_mapping.put(h_vals, int_list);
                  }
                  
                  end = System.nanoTime();
                  
                  population_time += (end - start) * 1.0/second2nano;
				  
				}

			}
		}
		
		Set<String> signatures = signiture_view_mappings_mappings.keySet();
        
        boolean matched = false;
        
        for(String curr_signature: signatures)
        {
          HashSet<Tuple> curr_view_mappings = signiture_view_mappings_mappings.get(curr_signature);
          
          if(valid_view_mapping_schema_level.size() == curr_view_mappings.size() && valid_view_mapping_schema_level.containsAll(curr_view_mappings))
          {
            covering_set_schema_level = c_view_map.get(curr_signature);
            
            matched = true;
            
            break;
          }
          
        }
        
        if(!matched)
        {
          
          HashSet<String> all_covered_subgoal_names = new HashSet<String>();
          
          HashSet<Argument> all_covered_args = new HashSet<Argument>();
          
          reasoning_min.get_all_covered_subgoals_args(all_covered_subgoal_names, all_covered_args, valid_view_mapping_schema_level);
          
//          HashSet<Tuple> c_unit_vec = get_citation_units_condition(valid_view_mapping_schema_level, query.body.size() + query.head.args.size(), query, all_covered_args, all_covered_subgoal_names, curr_views);
          
          HashSet<Tuple> selected_view_mappings = reasoning_min.greedy_algorithm(view_mapping_cost, valid_view_mapping_schema_level, all_covered_subgoal_names, all_covered_args, single_subgoal_relations, single_view_head_args);
          
          covering_set_schema_level = reasoning_min.get_valid_citation_combination(selected_view_mappings);

          
//          if(isclustering)
//            Covering_sets_clustering_by_relation.reasoning_single_tuple(head_variable_query_mapping, head_variable_view_mapping, relation_arg_mapping, valid_view_mapping_schema_level, query, rs, covering_set_schema_level, get_all_head_variables(valid_view_mapping_schema_level, query));
//          else
//            Covering_sets_no_clustering.reasoning_single_tuple(head_variable_query_mapping, head_variable_view_mapping, relation_arg_mapping, valid_view_mapping_schema_level, query, rs, covering_set_schema_level, get_all_head_variables(valid_view_mapping_schema_level, query));

        }
		
//		if(c_view_template != null)
//		{			
//			end_pos = tuple_num;
//			
//			
//			int [] interval = {start_pos, end_pos};
//			
//			
//			c_view_map.put(curr, c_view_template);
//			
//		}		
		
	}
	
	   static HashSet<Argument> get_all_head_variables(HashSet<Tuple> curr_valid_tuples, Query query)
	    {
	        HashSet<Argument> head_args = new HashSet<Argument>();
	        
	        for(Iterator iter = curr_valid_tuples.iterator(); iter.hasNext();)
	        {
	            Tuple tuple = (Tuple) iter.next();
	            
	            head_args.addAll(tuple.getArgs());
	        }
	        
	        head_args.retainAll(query.head.args);
	        
	        return head_args;
	    }
	
//	static void reasoning_single_tuple(HashSet views, Query query, ResultSet rs, HashSet<Covering_set> c_view_template, HashSet<Argument> curr_head_vars) throws ClassNotFoundException, SQLException
//    {
//        
////      ArrayList<citation_view_vector> view_com = new ArrayList<citation_view_vector>();
//        
//        HashSet<Covering_set> all_covering_sets = new HashSet<Covering_set>();
//        
//        Set<String> relations = head_variable_query_mapping.keySet();
//        
//        for(Iterator iter = relations.iterator(); iter.hasNext();)
//        {
//            String relation = (String) iter.next();
//            
//            HashSet<Argument> view_head_args = relation_arg_mapping.get(relation);
//            
//            if(view_head_args == null)
//            {
//                continue;
//            }
//            else
//            {
//                HashSet<Argument> view_head_args_copy = (HashSet<Argument>) view_head_args.clone();
//                
////              HashSet<citation_view_vector> covering_sets = new HashSet<citation_view_vector>();
//                
//                ArrayList<Argument> q_head_args = head_variable_query_mapping.get(relation);
//                
//                view_head_args_copy.retainAll(q_head_args);
//                
//                for(Iterator it = view_head_args_copy.iterator(); it.hasNext();)
//                {
//                    Argument arg = (Argument) it.next();
//                    
//                    ArrayList<Tuple> curr_tuples = (ArrayList<Tuple>) head_variable_view_mapping.get(arg).clone();
//                    
//                    curr_tuples.retainAll(views);
//                    
////                  System.out.println("tuples");
////                  
////                  for(Tuple tuple: curr_tuples)
////                  {
////                    System.out.print(tuple.name + ",");
////                  }
////                  
////                  System.out.println();
//                    
//                    all_covering_sets = Tuple_reasoning1_full_test_opt.join_views_curr_relation(curr_tuples, all_covering_sets, query.head.args, view_mapping);
//                    
////                  System.out.println(all_covering_sets);
//                }
//                
////              all_covering_sets = Tuple_reasoning1_full_test_opt.join_operation(all_covering_sets, covering_sets, all_covering_sets.size());
//            }
//            
//            
//            
//        }
//                
//        c_view_template.addAll(all_covering_sets);
//        
//        all_covering_sets.clear();
//        
//    }
    
//       static void reasoning_single_tuple_no_clustering(HashSet views, Query query, ResultSet rs, HashSet<Covering_set> c_view_template, HashSet<Argument> curr_head_vars) throws ClassNotFoundException, SQLException
//        {
//            
////        ArrayList<citation_view_vector> view_com = new ArrayList<citation_view_vector>();
//            
//            HashSet<Covering_set> all_covering_sets = new HashSet<Covering_set>();
//            
//            Set<Argument> args = head_variable_view_mapping.keySet();
//            
//            for(Argument arg: args)
//            {
//              ArrayList<Tuple> curr_tuples = (ArrayList<Tuple>) head_variable_view_mapping.get(arg).clone();
//              
//              curr_tuples.retainAll(views);
//              
//              all_covering_sets = Tuple_reasoning1_full_test_opt.join_views_curr_relation(curr_tuples, all_covering_sets, query.head.args, view_mapping);
//
//            }
//            
////          Set<String> relations = head_variable_query_mapping.keySet();
////          
////          for(Iterator iter = relations.iterator(); iter.hasNext();)
////          {
////              String relation = (String) iter.next();
////              
////              HashSet<Argument> view_head_args = relation_arg_mapping.get(relation);
////              
////              if(view_head_args == null)
////              {
////                  continue;
////              }
////              else
////              {
////                  HashSet<Argument> view_head_args_copy = (HashSet<Argument>) view_head_args.clone();
////                  
//////                  HashSet<citation_view_vector> covering_sets = new HashSet<citation_view_vector>();
////                  
////                  ArrayList<Argument> q_head_args = head_variable_query_mapping.get(relation);
////                  
////                  view_head_args_copy.retainAll(q_head_args);
////                  
////                  for(Iterator it = view_head_args_copy.iterator(); it.hasNext();)
////                  {
////                      Argument arg = (Argument) it.next();
////                      
////                      ArrayList<Tuple> curr_tuples = (ArrayList<Tuple>) head_variable_view_mapping.get(arg).clone();
////                      
////                      curr_tuples.retainAll(views);
////                      
////                      all_covering_sets = Tuple_reasoning1_full_test_opt.join_views_curr_relation(curr_tuples, all_covering_sets, query.head.args, view_mapping);
////                  }
////                  
//////                  all_covering_sets = Tuple_reasoning1_full_test_opt.join_operation(all_covering_sets, covering_sets, all_covering_sets.size());
////              }
////              
////              
////              
////          }
//                    
//            c_view_template.addAll(all_covering_sets);
//            
//            all_covering_sets.clear();
//            
//        }
//    

	
//	public static HashSet<String> gen_citation(Head_strs h_vals, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, JSONException
//	{
//		int index = 0;
//		
//		rs.beforeFirst();
//		
//		ArrayList<Integer> int_list = head_strs_rows_mapping.get(h_vals);
//		
////		System.out.println(int_list.toString());
//		
//		Set<int[]> intervals = c_view_map.keySet();
//		
//		HashSet<String> citations = new HashSet<String>();
//		
//		for(Iterator iter = intervals.iterator(); iter.hasNext();)
//		{
//			int [] interval = (int[]) iter.next();
//			
//			HashSet<citation_view_vector> c_views = c_view_map.get(interval);
//			
////			if(c_views.size() == 0)
////				citations.add("{\"db_name\":\"IUPHAR/BPS Guide to PHARMACOLOGY\",\"author\":[]}");
//			
//			for(int i = index; i < int_list.size(); i ++)
//			{
//				
//				
//				if(int_list.get(i) <= interval[1] && int_list.get(i) > interval[0])
//				{
//					rs.absolute(int_list.get(i));
//					
//					update_valid_citation_combination(c_views, rs, Resultset_prefix_col_num);
//					
////					System.out.println(c_views.toString());
//					
////					do_aggregate(curr_res, c_view, i, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, c, pst);
//					
//					citations.addAll(gen_citation(c_views, c, pst, view_query_mapping, populate_db.star_op, populate_db.plus_op));
//				}
//				
//				
//				if(int_list.get(i) >= interval[1])
//				{
//					index = i;
//					
//					break;
//				}
//			}
//		}
//		
//		return citations;
//	}

//	public static HashSet<String> gen_citation(Head_strs h_vals, HashMap<Head_strs, HashSet<String>> c_view_maps, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, JSONException
//	{
//		int index = 0;
//		
//		rs.beforeFirst();
//		
//		ArrayList<Integer> int_list = head_strs_rows_mapping.get(h_vals);
//		
////		System.out.println(int_list.toString());
//		
//		Set<int[]> intervals = c_view_map.keySet();
//		
//		HashSet<String> citations = new HashSet<String>();
//		
//		for(Iterator iter = intervals.iterator(); iter.hasNext();)
//		{
//			int [] interval = (int[]) iter.next();
//			
//			HashSet<citation_view_vector> c_views = c_view_map.get(interval);
//
//			for(int i = index; i < int_list.size(); i ++)
//			{
//				
//				
//				if(int_list.get(i) <= interval[1] && int_list.get(i) > interval[0])
//				{
//					rs.absolute(int_list.get(i));
//					
//					update_valid_citation_combination(c_views, rs, Resultset_prefix_col_num);
//										
//					if(c_view_maps.get(h_vals) == null)
//					{
//						
//						HashSet<String> c_view_values = new HashSet<String>();
//						
//						for(Iterator it = c_views.iterator(); it.hasNext();)
//						{
//							citation_view_vector covering_set = (citation_view_vector) it.next();
//							
//							String v_str = covering_set.toString();
//							
//							c_view_values.add(v_str);
//						}
//						
//						c_view_maps.put(h_vals, c_view_values);
//					}
//					else
//					{
//						HashSet<String> c_view_values = c_view_maps.get(h_vals);
//						
//						for(Iterator it = c_views.iterator(); it.hasNext();)
//						{
//							citation_view_vector covering_set = (citation_view_vector) it.next();
//							
//							String v_str = covering_set.toString();
//							
//							c_view_values.add(v_str);
//						}
//						
//						c_view_maps.put(h_vals, c_view_values);
//					}
//					
//					
//					
////					System.out.println(c_views.toString());
//					
////					do_aggregate(curr_res, c_view, i, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, c, pst);
//					
//					citations.addAll(gen_citation(c_views, c, pst, view_query_mapping, populate_db.star_op, populate_db.plus_op));
//				}
//				
//				
//				if(int_list.get(i) >= interval[1])
//				{
//					index = i;
//					
//					break;
//				}
//			}
//		}
//		
//		return citations;
//	}
//	
//	public static HashSet<String> gen_citation(Head_strs h_vals, HashMap<Head_strs, HashSet<String>> c_view_maps, HashSet<String> covering_set_strs, Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException, JSONException
//	{
//		int index = 0;
//		
//		rs.beforeFirst();
//		
//		ArrayList<Integer> int_list = head_strs_rows_mapping.get(h_vals);
//		
////		System.out.println(int_list.toString());
//		
//		Set<int[]> intervals = c_view_map.keySet();
//		
//		HashSet<String> citations = new HashSet<String>();
//		
//		for(Iterator iter = intervals.iterator(); iter.hasNext();)
//		{
//			int [] interval = (int[]) iter.next();
//			
//			HashSet<citation_view_vector> c_views = c_view_map.get(interval);
//
//			for(int i = index; i < int_list.size(); i ++)
//			{
//				
//				
//				if(int_list.get(i) <= interval[1] && int_list.get(i) > interval[0])
//				{
//					rs.absolute(int_list.get(i));
//					
//					update_valid_citation_combination(c_views, rs, Resultset_prefix_col_num);
//					
//					for(Iterator it = c_views.iterator(); iter.hasNext();)
//					{
//						String c_view_index = new String();
//						
//						citation_view_vector covering_set = (citation_view_vector) it.next();
//						
//						for(citation_view view_mapping: covering_set.c_vec)
//						{
//							c_view_index += view_mapping.get_name() + populate_db.separator + view_mapping.get_table_name_string();
//						}
//						
//						covering_set_strs.add(c_view_index);
//					}
//										
//					if(c_view_maps.get(h_vals) == null)
//					{
//						
//						HashSet<String> c_view_values = new HashSet<String>();
//						
//						for(Iterator it = c_views.iterator(); iter.hasNext();)
//						{
//							citation_view_vector covering_set = (citation_view_vector) it.next();
//							
//							String v_str = covering_set.toString();
//							
//							c_view_values.add(v_str);
//						}
//						
//						c_view_maps.put(h_vals, c_view_values);
//					}
//					else
//					{
//						HashSet<String> c_view_values = c_view_maps.get(h_vals);
//						
//						for(Iterator it = c_views.iterator(); iter.hasNext();)
//						{
//							citation_view_vector covering_set = (citation_view_vector) it.next();
//							
//							String v_str = covering_set.toString();
//							
//							c_view_values.add(v_str);
//						}
//						
//						c_view_maps.put(h_vals, c_view_values);
//					}
//					
//					
//					
////					System.out.println(c_views.toString());
//					
////					do_aggregate(curr_res, c_view, i, author_lists, view_query_mapping, query_lambda_str, author_mapping, max_num, c, pst);
//					
//					citations.addAll(gen_citation(c_views, c, pst, view_query_mapping, populate_db.star_op, populate_db.plus_op));
//				}
//				
//				
//				if(int_list.get(i) >= interval[1])
//				{
//					index = i;
//					
//					break;
//				}
//			}
//		}
//		
//		return citations;
//	}
//	
	static HashSet<String> gen_citation(HashSet<Covering_set> c_views, Connection c, PreparedStatement pst, HashMap<String, HashMap<String, String>> view_query_mapping, String star_op, String plus_op) throws ClassNotFoundException, SQLException, JSONException
	{
		HashSet<String> citations = new HashSet<String>();
				
		HashMap<String, HashMap<String, HashSet<String>>> view_author_mapping = new HashMap<String, HashMap<String, HashSet<String>>>();
		
		for(Iterator iter = c_views.iterator(); iter.hasNext();)
		{
			
			Covering_set covering_set = (Covering_set) iter.next();
									
			HashSet<String> str = gen_citation0.get_citations3(covering_set, c, pst, view_query_mapping, author_mapping, max_author_num, citation_queries, view_author_mapping, star_op);
			
			citations.addAll(str);		
		}
		
		return citations;
	}
	
//	static HashSet<String> populate_citation(Vector<citation_view_vector2> update_c_view, Vector<String> vals, Connection c, PreparedStatement pst, HashMap<String, HashSet<String>> citation_strings, Head_strs h_vals) throws SQLException, ClassNotFoundException
//	{
//		HashSet<String> citations = new HashSet<String>();
//		
//		HashSet<String> author_list = new HashSet<String>();
//		
//		for(int p =0; p<update_c_view.size(); p++)
//		{
//			
//			author_list = new HashSet<String>();
//			
//			String str = gen_citation1.populate_citation(update_c_view.get(p), vals, c, pst, citation_strings, max_author_num, author_list);
//
//			citations.add(str);
//			
//			if(authors.get(h_vals) == null)
//			{
//				Vector<HashSet<String>> author_l = new Vector<HashSet<String>>();
//				
//				author_l.add(author_list);
//				
//				authors.put(h_vals, author_l);
//				
//			}
//			else
//			{
//				Vector<HashSet<String>> author_l = authors.get(h_vals);
//				
//				author_l.add(author_list);
//				
//				authors.put(h_vals, author_l);
//			}
//			
//			
//			
////			HashMap<String, Vector<String>> query_str = new HashMap<String, Vector<String>>();
////			
////			String str = gen_citation1.get_citations(c_unit_combinaton.get(p), vals, c, pst, query_str);
////
////			citations.add(str);
//			
////			citation_strings.add(query_str);
//			
//		}
//		
////		citation_strs.add(citations);
//		
//		return citations;
//	}
//	
	public static void output_vec(Vector<Vector<citation_view>> c_unit_vec)
	{
		for(int i = 0; i<c_unit_vec.size(); i++)
		{
			Vector<citation_view> c_vec = c_unit_vec.get(i);
			
			System.out.print("[");
			
			for(int j = 0; j<c_vec.size(); j++)
			{
				citation_view c = c_vec.get(j);
				
				if(j>=1)
					System.out.print(",");
				
				System.out.print(c.toString());
			}
			System.out.print("]");
		}
		
		System.out.println();
	}
	
	
	public static void output_vec_com(Vector<Covering_set>c_unit_combinaton)
	{
		for(int i = 0; i<c_unit_combinaton.size(); i++)
		{
			if(i >= 1)
				System.out.print(",");
			
			System.out.print("view_combination:::" + c_unit_combinaton.get(i));
		}
		System.out.println();
	}
	
	public static void update_valid_citation_combination(HashSet<Covering_set> insert_c_view, ResultSet rs, int start_pos) throws SQLException
	{
		
//		HashSet<citation_view_vector2> update_c_views = new HashSet<citation_view_vector2>();
		
		for(Iterator iter = insert_c_view.iterator(); iter.hasNext();)
		{
			
			Covering_set c_vec = (Covering_set) iter.next();
			
			for(citation_view view_mapping: c_vec.c_vec)
			{
				citation_view c_view = view_mapping;
				
				if(c_view.has_lambda_term())
				{
					citation_view_parametered curr_c_view = (citation_view_parametered) c_view;
					
					Vector<Lambda_term> lambda_terms = curr_c_view.lambda_terms;
					
					for(int k = 0; k<lambda_terms.size(); k++)
					{
						int id = lambda_term_id_mapping.get(lambda_terms.get(k).toString());
						
						curr_c_view.put_lambda_paras(lambda_terms.get(k), rs.getString(id + start_pos + 1));
					}
					
				}
				
			}
			
//			c_vec = new citation_view_vector2(c_vec.c_vec);
			
//			update_c_views.add(c_vec);
			
//			insert_c_view.remove(i);
//			
//			insert_c_view.add(i, c_vec);
		}
		
//		insert_c_view.clear();
		
//		return update_c_views;
		
		
				
//		insert_c_view = remove_duplicate_final(insert_c_view);
//
//		insert_c_view = remove_duplicate(insert_c_view);
		
//		Vector<citation_view_vector2> update_c_views = new Vector<citation_view_vector2>();
//		
//		for(int i = 0; i<insert_c_view.size();i++)
//		{
//			citation_view_vector2 insert_c_view_vec = insert_c_view.get(i);
//			
//			Vector<citation_view> update_c_view_vec = new Vector<citation_view>();
//			
//			for(int j = 0; j < insert_c_view_vec.c_vec.size(); j++)
//			{
//				citation_view c = insert_c_view_vec.c_vec.get(j);
//				
//				c = c_unit_vec.get(String.valueOf(c.get_index()));
//				
//				update_c_view_vec.add(c);
//			}
//			
//			update_c_views.add(new citation_view_vector2(update_c_view_vec, insert_c_view_vec.index_vec, insert_c_view_vec.view_strs));
//		}
		
//		return insert_c_view;
	}
	
//	public static HashSet<String> cal_unique_covering_sets() throws SQLException
//	{
//		
////		HashSet<citation_view_vector2> update_c_views = new HashSet<citation_view_vector2>();
//		
//		HashSet<String> unique_covering_sets = new HashSet<String>();
//		
//		if(agg_intersection)
//		{
//			ArrayList<citation_view_vector> covering_sets = Aggregation6.curr_res;
//			
//			HashSet<citation_view_vector> all_covering_sets = new HashSet<citation_view_vector>();
//			
//			all_covering_sets.addAll(covering_sets);
//			
//			for(int i = 0; i < tuple_num; i++)
//			{
//				rs.absolute(i + 1);
//				
//				update_valid_citation_combination(all_covering_sets, rs, Resultset_prefix_col_num);
//				
//				for(Iterator it = all_covering_sets.iterator(); it.hasNext();)
//				{
//					citation_view_vector covering_set = (citation_view_vector) it.next();
//					
//					unique_covering_sets.add(covering_set.toString());
//				}
//				
//			}
//		}
//		else
//		{
//			Set<String> keys = c_view_map.keySet();
//			
//			
//			for(Iterator iter = keys.iterator(); iter.hasNext();)
//			{
//				String curr_key = (String) iter.next();
//				
//				HashSet<citation_view_vector> covering_sets = c_view_map.get(curr_key);
//				
//				for(int i = curr_key[0]; i < curr_key[1]; i++)
//				{
//					rs.absolute(i + 1);
//					
//					update_valid_citation_combination(covering_sets, rs, Resultset_prefix_col_num);
//					
//					for(Iterator it = covering_sets.iterator(); it.hasNext();)
//					{
//						citation_view_vector covering_set = (citation_view_vector) it.next();
//						
//						unique_covering_sets.add(covering_set.toString());
//					}
//				}				
//			}
//		}
//		
//		return unique_covering_sets;
//	}
//	
	public static void get_views_parameters(HashSet<Tuple> insert_c_view, ResultSet rs, int start_pos, HashMap<Tuple, ArrayList<Head_strs>> values) throws SQLException
	{
		
//		HashSet<citation_view_vector2> update_c_views = new HashSet<citation_view_vector2>();
		
		Set<String> head_sets = lambda_term_id_mapping.keySet();
		
		for(Tuple view_mapping: insert_c_view)
		{
			
//			citation_view c_vec = insert_c_view.get(i);
			
//			for(int j = 0; j<c_vec.c_vec.size(); j++)
//			{
//				citation_view c_view = c_vec.c_vec.get(j);
				
				if(view_mapping.lambda_terms.size() > 0)
				{
//					citation_view_parametered curr_c_view = (citation_view_parametered) c_vec;
					
//					Vector<Lambda_term> lambda_terms = curr_c_view.lambda_terms;
					
					Vector<String> heads = new Vector<String>();
					
					for(int k = 0; k<view_mapping.lambda_terms.size(); k++)
					{
						int id = lambda_term_id_mapping.get(view_mapping.lambda_terms.get(k).toString());
						
						heads.add(rs.getString(id + start_pos + 1));
						
//						curr_c_view.put_lambda_paras(lambda_terms.get(k), rs.getString(id + start_pos + 1));
					}
					
					Head_strs head_values = new Head_strs(heads);
					
					if(values.get(view_mapping) == null)
					{
					  
					  ArrayList<Head_strs> curr_head_values = new ArrayList<Head_strs>();
					  
					  curr_head_values.add(head_values);
					  
						values.put(view_mapping, curr_head_values);
					}
					else
					{
//						HashSet<Head_strs> curr_head_values = new HashSet<Head_strs>();
//						
//						curr_head_values.add(head_values);
						
						values.get(view_mapping).add(head_values);//add(curr_head_values);
					}
					
				}
//				else
//				{
//					if(values.size() > i)
//					{
//						continue;
//					}
//					else
//					{
//						values.add(new HashSet<Head_strs>());
//					}
//				}
				
//			}
		}
	}
	
	public static HashSet<String> gen_citations_per_covering_set(Covering_set c_views, ResultSet rs, int start_pos, int tuple_id, Connection c, PreparedStatement pst) throws SQLException, JSONException
    {
        
//      HashSet<citation_view_vector2> update_c_views = new HashSet<citation_view_vector2>();
        
        HashMap<String, HashMap<String, HashSet<String>>> view_author_mapping = new HashMap<String, HashMap<String, HashSet<String>>>();
	  
	    HashSet<String> citations = new HashSet<String>();
	  
        Set<String> head_sets = lambda_term_id_mapping.keySet();
        
//        for(Iterator iter = c_views.iterator(); iter.hasNext();)
        {
          Covering_set covering_set = c_views;
          
          for(citation_view view_mapping: covering_set.c_vec)
          {
            citation_view view = view_mapping;
            
            if(view.has_lambda_term())
            {
                citation_view_parametered curr_c_view = (citation_view_parametered) view;
                
                Vector<Lambda_term> lambda_terms = curr_c_view.lambda_terms;
                
                Vector<String> heads = new Vector<String>();
                
                for(int k = 0; k<lambda_terms.size(); k++)
                {
                    int id = lambda_term_id_mapping.get(lambda_terms.get(k).toString());
                    
                    heads.add(rs.getString(id + start_pos + 1));
                    
                    curr_c_view.put_lambda_paras(lambda_terms.get(k), rs.getString(id + start_pos + 1));
                }                
            }
          }
          
          HashSet<String> curr_citations = gen_citation0.get_citations3(covering_set, c, pst, view_query_mapping, author_mapping, max_author_num, citation_queries, view_author_mapping, populate_db.star_op);
          
          citations.addAll(curr_citations);
          
        }
        

        return citations;
//        for(int i = 0; i<insert_c_view.size(); i++)
//        {
//            
//            citation_view c_vec = insert_c_view.get(i);
//            
////          for(int j = 0; j<c_vec.c_vec.size(); j++)
////          {
////              citation_view c_view = c_vec.c_vec.get(j);
//                
//                if(c_vec.has_lambda_term())
//                {
//                    citation_view_parametered curr_c_view = (citation_view_parametered) c_vec;
//                    
//                    Vector<Lambda_term> lambda_terms = curr_c_view.lambda_terms;
//                    
//                    Vector<String> heads = new Vector<String>();
//                    
//                    for(int k = 0; k<lambda_terms.size(); k++)
//                    {
//                        int id = lambda_term_id_mapping.get(lambda_terms.get(k).toString());
//                        
//                        heads.add(rs.getString(id + start_pos + 1));
//                        
//                        curr_c_view.put_lambda_paras(lambda_terms.get(k), rs.getString(id + start_pos + 1));
//                    }
//                    
//                    Head_strs head_values = new Head_strs(heads);
//                    
//                    if(values.size() > i)
//                    {
//                        values.get(i).add(head_values);
//                    }
//                    else
//                    {
//                        HashSet<Head_strs> curr_head_values = new HashSet<Head_strs>();
//                        
//                        curr_head_values.add(head_values);
//                        
//                        values.add(curr_head_values);
//                    }
//                    
//                }
//                else
//                {
//                    if(values.size() > i)
//                    {
//                        continue;
//                    }
//                    else
//                    {
//                        values.add(new HashSet<Head_strs>());
//                    }
//                }
                
//          }
//        }
    }
	
	
	public static void get_views_parameters(ArrayList<citation_view> insert_c_view, ResultSet rs, int start_pos, ArrayList<HashSet<Head_strs>> values) throws SQLException
	{
		
		
		for(int j = 0; j<insert_c_view.size(); j++)
		{
			
//			int i = insert_c_view.get(j);
			
			citation_view c_vec = insert_c_view.get(j);
			
//			for(int j = 0; j<c_vec.c_vec.size(); j++)
//			{
//				citation_view c_view = c_vec.c_vec.get(j);
				
				if(c_vec.has_lambda_term())
				{
					citation_view_parametered curr_c_view = (citation_view_parametered) c_vec;
					
					Vector<Lambda_term> lambda_terms = curr_c_view.lambda_terms;
					
					Vector<String> heads = new Vector<String>();
					
					for(int k = 0; k<lambda_terms.size(); k++)
					{
						int id = lambda_term_id_mapping.get(lambda_terms.get(k).toString());
						
						heads.add(rs.getString(id + start_pos + 1));
						
						curr_c_view.put_lambda_paras(lambda_terms.get(k), rs.getString(id + start_pos + 1));
					}
					
					Head_strs head_values = new Head_strs(heads);
					
//					if(values.size() > i)
					{
						values.get(j).add(head_values);
					}
//					else
//					{
//						HashSet<Head_strs> curr_head_values = new HashSet<Head_strs>();
//						
//						curr_head_values.add(head_values);
//						
//						values.add(curr_head_values);
//					}
					
				}
//				else
//				{
//					if(values.size() > i)
//					{
//						continue;
//					}
//					else
//					{
//						values.add(new HashSet<Head_strs>());
//					}
//				}
				
//			}
		}
	}
	
	
	static Vector<Lambda_term> get_lambda_terms(String c_view_name, Connection c, PreparedStatement pst) throws SQLException
	{
		Vector<Lambda_term> lambda_terms = new Vector<Lambda_term>();
		
		String q = "select lambda_term, table_name from view2lambda_term where view = '" + c_view_name + "'";
		
		pst = c.prepareStatement(q);
		
		ResultSet r = pst.executeQuery();
		
		if(r.next())
		{
			String [] lambda_term_names = r.getString(1).split(populate_db.separator);
			
			String [] table_name = r.getString(2).split(populate_db.separator);
			
			for(int i = 0; i<lambda_term_names.length; i++)
			{
				Lambda_term l_term = new Lambda_term(lambda_term_names[i], table_name[i]);
				
				lambda_terms.add(l_term);
				
			}
			
			
		}
		
		return lambda_terms;
		
		
		
	}
	
//	public static Vector<Vector<citation_view>> get_citation_units(Vector<String[]> c_units, HashSet<String> invalid_citation_unit, HashMap<String, Vector<Integer>> table_pos_map, ResultSet rs, int start_pos) throws ClassNotFoundException, SQLException
//	{
//		Vector<Vector<citation_view>> c_views = new Vector<Vector<citation_view>>();
//		
//		for(int i = 0; i<c_units.size(); i++)
//		{
//			Vector<citation_view> c_view = new Vector<citation_view>();
//			String [] c_unit_str = c_units.get(i);
//			for(int j = 0; j<c_unit_str.length; j++)
//			{
//				if(c_unit_str[j].contains("(") && c_unit_str[j].contains(")"))
//				{
//					
//					String c_view_name = c_unit_str[j].split("\\(")[0];
//					
//					if(invalid_citation_unit.contains(c_view_name))
//						continue;
//					
//					
//					Vector<String> c_view_paras = new Vector<String>();
//					
//					
////					if(view_type_map.get(c_view_name))
//					{
//						c_view_paras.addAll(Arrays.asList(c_unit_str[j].split("\\(")[1].split("\\)")[0].split(",")));
//						
//						citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name, false);
//						
//						int k = 0;
//						
//						for(k = 0; k<c_view_paras.size(); k++)
//						{
//							
//							String t_name = curr_c_view.lambda_terms.get(k).table_name;
//							
//							Vector<Integer> positions = table_pos_map.get(t_name);
//							
//							int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(k).name);
//							
//							int[] final_pos = new int[1];
//							
//							if(check_value(c_view_paras.get(k), start_pos, positions, rs, offset, final_pos))
//							{
//								
//								curr_c_view.lambda_terms.get(k).id = final_pos[0];
//								
//								continue;
//							}
//							else
//								break;
//							
//						}
//						
//						if(k < c_view_paras.size())
//							continue;
//						
//						
//						curr_c_view.put_paramters(c_view_paras);
//						
//						c_view.add(curr_c_view);
//						
//					}
//					
////					else
////					{
////						String []curr_c_unit = c_unit_str[j].split("\\(")[1].split("\\)");
////												
////						citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name, false);
////						
////						if(curr_c_unit.length == 0)
////						{
////							int [] final_pos = new int[1];
////							
////							String t_name = curr_c_view.lambda_terms.get(0).table_name;
////							
////							Vector<Integer> positions = table_pos_map.get(t_name);
////							
////							int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(0).name);
////							
////							Vector<Integer> ids = new Vector<Integer>();
////							
////							Vector<String> vals = get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
////							
////							for(int p = 0; p<vals.size(); p++)
////							{								
////								citation_view_parametered inserted_view = new citation_view_parametered(c_view_name, false);
////								
////								Vector<String> val = new Vector<String>();
////								
////								
////								val.add(vals.get(p));
////								
////								inserted_view.put_paramters(val);
////								
////								inserted_view.lambda_terms.get(p).id = ids.get(p);
////								
////								c_view.add(inserted_view);
////							}							
////						}
////						else
////						{
////							String [] c_v_unit = curr_c_unit[0].split(",");
////													
////							Vector<Vector<String>> values = new Vector<Vector<String>>();
////							
////							Vector<Vector<Integer>> id_arrays = new Vector<Vector<Integer>>();
////							
////							for(int p = 0; p<c_v_unit.length; p++)
////							{								
////								if(c_v_unit[p].isEmpty())
////								{
////									int [] final_pos = new int[1];
////									
////									String t_name = curr_c_view.lambda_terms.get(p).table_name;
////									
////									Vector<Integer> positions = table_pos_map.get(t_name);
////									
////									int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(p).name);
////									
////									Vector<Integer> ids = new Vector<Integer>();
////									
////									Vector<String> vals = get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
////									
////									values = update_views(values, vals, id_arrays, ids);
////									
////									
////								}
////								else
////								{
////									
////									String t_name = curr_c_view.lambda_terms.get(0).table_name;
////									
////									Vector<Integer> positions = table_pos_map.get(t_name);
////									
////									int offset = table_lambda_term_seq.get(t_name).get(curr_c_view.lambda_terms.get(p).name);
////									
////									int[] final_pos = new int[1];
////									
////									Vector<Integer> ids = new Vector<Integer>();
////									
////									get_curr_c_view(positions, start_pos, offset, final_pos, ids, rs);
////									
////									id_arrays.add(ids);
////									
////									if(!values.isEmpty())
////									{
////										for(int t = 0; t<values.size(); t++)
////										{
////											Vector<String> v = values.get(t);
////											
////											v.add(c_v_unit[p]);
////										}
////									}
////									else
////									{
////										Vector<String> v= new Vector<String>();
////										
////										v.add(c_v_unit[p]);
////										
////										values.add(v);
////									}
////									
////									
////								}
////							}
////							
////							for(int p = 0; p<values.size(); p++)
////							{
////								citation_view_parametered inserted_view = new citation_view_parametered(c_view_name, false);
////								
////								Vector<String> val = values.get(p);
////								
////								Vector<Integer> ids = id_arrays.get(p);
////															
////								inserted_view.put_paramters(val);
////								
////								for(int w = 0; w < ids.size(); w++)
////								{
////									inserted_view.lambda_terms.get(w).id = ids.get(w);
////								}
////								
////								c_view.add(inserted_view);
////							}
////							
////						}
////						
////						
////						
//////						HashMap<String, Integer> lambda_term_map = view_lambda_term_map.get(c_view_name);
//////						
//////						Set name_set = lambda_term_map.entrySet();
//////						
//////						for(Iterator iter = name_set.iterator(); iter.hasNext(); )
//////						{
//////							
//////						}
//////						
//////						if(curr_c_unit.isEmpty())
//////						{
//////							
//////						}
//////						else
//////						{
//////							String [] curr_c_units = curr_c_unit.split(",");
//////							
//////							for(int t = 0; t<curr_c_units.length; t++)
//////							{
//////								if(curr_c_units)
//////							}
//////							
//////							if(curr_c_units)
//////							
//////						}
////						
////					}
//					
//					
//
//
//					
////					curr_c_view.lambda_terms.
//					
//				}
//				else
//				{
//					if(invalid_citation_unit.contains(c_unit_str[j]))
//						continue;
//					
//					c_view.add(new citation_view_unparametered(c_unit_str[j]));
//				}
//			}
//			
//			c_views.add(c_view);
//		}
//		
//		return c_views;
//	}
//	
	static Vector<Vector<String>> update_views(Vector<Vector<String>> values, Vector<String> v, Vector<Vector<Integer>> id_arrays, Vector<Integer> ids)
	{
		Vector<Vector<String>> update_values = new Vector<Vector<String>>();
		
		Vector<Vector<Integer>> update_indexes = new Vector<Vector<Integer>>();
		
		if(values.isEmpty())
		{
			update_values.add(v);
			
			id_arrays.add(ids);
			
			return update_values;
		}
		
		
		for(int i = 0; i<values.size(); i++)
		{
			
			Vector<String> value = values.get(i);
			
			Vector<Integer> curr_ids = id_arrays.get(i);
			
			for(int j = 0; j<v.size(); j++)
			{
				Vector<String> curr_values = new Vector<String>();
				
				Vector<Integer> c_ids = new Vector<Integer>();
				
				curr_values.addAll(value);
				
				c_ids.addAll(curr_ids);
				
				curr_values.add(v.get(j));
				
				c_ids.add(ids.get(j));
				
				update_indexes.add(c_ids);
				
				update_values.add(curr_values);
			}
		}
		
		id_arrays = update_indexes;
		
		return update_values;
	}
	
	static Vector<String> get_curr_c_view(Vector<Integer> positions, int start_pos, int offset, int [] final_pos, Vector<Integer> ids, ResultSet rs) throws SQLException
	{
		
		Vector<String> values = new Vector<String>();
		
		for(int i = 0; i<positions.size(); i++)
		{
			int position = start_pos + positions.get(i) + offset;
			
			String value = rs.getString(position);
			
			ids.add(position);
			
			values.add(value);
			
		}
		
		return values;
	}
	
	static boolean correct_tuple(Tuple tuple, Subgoal subgoal)
	{
		boolean correct = false;
		
//		System.out.println("query:::" + tuple.query);
				
		HashSet<Subgoal> subgoals = tuple.getTargetSubgoals();  
		
		for(Iterator iter = subgoals.iterator(); iter.hasNext();)
		{
			
			Subgoal curr_subgoal = (Subgoal)iter.next();
			
//			System.out.println("1::::::" + subgoal.toString());
//			
//			System.out.println("2::::::" + curr_subgoal.toString());
			
			if(curr_subgoal.toString().equals(subgoal.toString()))
				return true;
		}
		
		return correct;
	}
	
	public static HashSet<Tuple> get_citation_units_condition(ArrayList<HashSet<String>> c_units, int start_pos, Query q, HashSet<Argument> all_head_vars, HashSet<String> all_mapped_subgoals, HashSet<Tuple> curr_tuple_mapping) throws SQLException
	{
		HashSet<Tuple> c_views = new HashSet<Tuple>();
		
//		boolean parameterized = true;
		
		HashMap<String, citation_view> citation_view_mapping = new HashMap<String, citation_view>();
		
		for(int i = 0; i<c_units.size(); i++)
		{
			ArrayList<citation_view> c_view = new ArrayList<citation_view>();
			
			Subgoal subgoal = (Subgoal) q.body.get(i);
			
			HashSet<Argument> curr_head_vars = new HashSet<Argument>();
			
			HashSet<String> curr_subgoal_names = new HashSet<String>();
			
			HashSet<String> c_unit_str = c_units.get(i);
			
			ArrayList<Tuple> curr_valid_tuples = new ArrayList<Tuple>();
			
			for(Iterator it = c_unit_str.iterator(); it.hasNext();)
			{
				
				String annotation_name = (String) it.next();
								
				String c_view_name = annotation_name.split("\\(")[0];
		
//				ArrayList<Tuple> available_tuples = curr_tuple_mapping.get(c_view_name);
					
//					if(available_tuples != null)
					{						
						
						String view_tuple_mapping_key = c_view_name + populate_db.separator + subgoal.name;
						
						ArrayList<Tuple> valid_tuples = new ArrayList<Tuple>();
						
						if(view_tuple_mapping.get(view_tuple_mapping_key) == null)
							continue;
						
						valid_tuples.addAll(view_tuple_mapping.get(view_tuple_mapping_key));
						
//						ArrayList<Tuple> available_tuples = curr_tuple_mapping.get(c_view_name);
						
						valid_tuples.retainAll(curr_tuple_mapping);
						
						
						
						for(int k = 0; k<valid_tuples.size(); k++)
						{
							
							Tuple curr_tuple = valid_tuples.get(k);
							
							HashSet<Subgoal> subgoals = curr_tuple.getTargetSubgoals();
							
							int counter = 0;
							
							if(subgoals.size() > 1)
							{
								for(Iterator curr_iter = subgoals.iterator(); curr_iter.hasNext();)
								{
									Subgoal curr_subgoal = (Subgoal) curr_iter.next();
									
									int subgoal_id = q_subgoal_id.get(curr_subgoal.name);
									
									HashSet<String> curr_c_unit_str = c_units.get(subgoal_id);
									
									if(!curr_c_unit_str.contains(annotation_name))
									{
										break;
									}
									
									counter ++;
								}
								
								if(counter < subgoals.size())
								{
									valid_tuples.remove(k);
									
									k --;
									
									continue;
								}
							}
							
							
							
							Vector<Argument> head_args = curr_tuple.getArgs();
							
							curr_head_vars.addAll(head_args);
							
							curr_subgoal_names.addAll(curr_tuple.getTargetSubgoal_strs());
							
//							for(int p = 0; p<head_args.size(); p++)
//							{
//								curr_head_vars.add(head_args.get(i).toString());
//							}
						}
						
						c_views.addAll(valid_tuples);
//						for(int k = 0; k<valid_tuples.size(); k++)
//						{
//							Tuple valid_tuple = valid_tuples.get(k);
//							
////							if(valid_tuple.mapSubgoals_str.get(q.subgoal_name_mapping.get(subgoal.name)).equals(subgoal.name))
//							{								
//								String key = valid_tuple.name + populate_db.separator + valid_tuple.mapSubgoals_str.toString();
//								
//								if(citation_view_mapping.get(key) != null)
//								{
//									citation_view exist_view = citation_view_mapping.get(key);
//									
//									Vector<Argument> head_args = exist_view.get_view_tuple().getArgs();
//									
//									for(int p = 0; p<head_args.size(); p++)
//									{
//										curr_head_vars.add(head_args.get(i).toString());
//									}
//									
//									c_view.add(exist_view);
//									
//									continue;
//								}
//								else
//								{
//									if(valid_tuple.lambda_terms.size() > 0)
//									{
//										
//										citation_view_parametered c = new citation_view_parametered(c_view_name, view_mapping.get(c_view_name), valid_tuple);
//					
//										Vector<Argument> head_args = c.view_tuple.getArgs();
//										
//										for(int p = 0; p<head_args.size(); p++)
//										{
//											curr_head_vars.add(head_args.get(i).toString());
//										}
//										
//										citation_view_mapping.put(key, c);
//										
//										c_view.add(c);
//									}	
//									else
//									{
//										
//										citation_view_unparametered c = new citation_view_unparametered(c_unit_str[j], valid_tuple);
//										
//										Vector<Argument> head_args = c.view_tuple.getArgs();
//										
//										for(int p = 0; p<head_args.size(); p++)
//										{
//											curr_head_vars.add(head_args.get(i).toString());
//										}
//										
//										citation_view_mapping.put(key, c);
//										
//										c_view.add(c);
//									}
//								}
//								
//								
//							}
//						}
					}
			}
			
			all_mapped_subgoals.addAll(curr_subgoal_names);
			
//			c_views.add(curr_valid_tuples);
			
			all_head_vars.addAll(curr_head_vars);
			
//			c_views.add(c_view);
		}
		
//		System.out.println(all_head_vars);
		
		return c_views;
	}
	
	
	static boolean check_value(String paras, int start_pos, Vector<Integer> positions, ResultSet rs, int offset, int[] final_pos) throws SQLException
	{
		for(int i = 0; i<positions.size(); i++)
		{
			int position = start_pos + positions.get(i) + offset;
			
			String value = rs.getString(position);
			
			if(value.equals(paras))
			{
				final_pos[0] = position;
				return true;
			}
			
		}
		
		return false;
	}
	
//	public static HashMap<String,citation_view> get_citation_units_unique(Vector<String[]> c_units) throws ClassNotFoundException, SQLException
//	{
////		Vector<citation_view> c_views = new Vector<citation_view>();
//		
//		HashMap<String,citation_view> c_view_map = new HashMap<String, citation_view>();
//		
//		for(int i = 0; i<c_units.size(); i++)
//		{
////			Vector<citation_view> c_view = new Vector<citation_view>();
//			String [] c_unit_str = c_units.get(i);
//			for(int j = 0; j<c_unit_str.length; j++)
//			{
//				if(c_unit_str[j].contains("(") && c_unit_str[j].contains(")"))
//				{
//					
//					String c_view_name = c_unit_str[j].split("\\(")[0];
//					
//					Vector<String> c_view_paras = new Vector<String>();
//					
//					c_view_paras.addAll(Arrays.asList(c_unit_str[j].split("\\(")[1].split("\\)")[0].split(",")));
//					
//					citation_view_parametered curr_c_view = new citation_view_parametered(c_view_name);
//					
//					curr_c_view.put_paramters(c_view_paras);
//					
//					c_view_map.put(String.valueOf(curr_c_view.get_index()),curr_c_view);
//				}
//				else
//				{
//					citation_view_unparametered curr_c_view = new citation_view_unparametered(c_unit_str[j]);
//					
//					c_view_map.put(String.valueOf(curr_c_view.get_index()),curr_c_view);
//				}
//			}
//			
//		}
//		
////		Set set = c_view_map.keySet();
////		
////		for(Iterator iter = set.iterator();iter.hasNext();)
////		{
////			String key = (String) iter.next();
////			
////			c_views.add(c_view_map.get(key));
////		}
//		
//		return c_view_map;
//	}
//	
	static void find_max_covering(String target_relation_name, HashSet<Integer> all_head_vars)
	{
		ArrayList<Integer> query_head_curr_relations = head_variable_query_mapping.get(target_relation_name);
		
		if(query_head_curr_relations == null)
		{
			all_head_vars.clear();
			
			return;
		}
		
		all_head_vars.retainAll(query_head_curr_relations);		
		
	}
	
	public static HashSet<Covering_set> join_views_curr_relation(ArrayList<Tuple> tuples, HashSet<Covering_set> curr_view_com, HashSet<Integer> curr_head_args)
	{
		if(curr_view_com.isEmpty())
		{
			if(tuples.isEmpty())
				return new HashSet<Covering_set>();
			else
			{
				HashSet<Covering_set> new_view_com = new HashSet<Covering_set>();
				
				for(int i = 0; i<tuples.size(); i++)
				{
					
					Tuple valid_tuple = (Tuple) tuples.get(i).clone();
					
					valid_tuple.args.retainAll(curr_head_args);
					
					if(valid_tuple.lambda_terms.size() > 0)
					{
						
						citation_view_parametered c = new citation_view_parametered(valid_tuple.name, valid_tuple.query, valid_tuple);
						
						Covering_set curr_views = new Covering_set(c);
						
						remove_duplicate(new_view_com, curr_views);
					}	
					else
					{
						
						citation_view_unparametered c = new citation_view_unparametered(valid_tuple.name, valid_tuple);
						
						Covering_set curr_views = new Covering_set(c);
						
						remove_duplicate(new_view_com, curr_views);
					}
				}
				
				return new_view_com;
			}
		}
		
		else
		{
			HashSet<Covering_set> new_view_com = new HashSet<Covering_set>();
			
			for(int i = 0; i<tuples.size(); i++)
			{
				Tuple valid_tuple = (Tuple) tuples.get(i).clone();
				
				valid_tuple.args.retainAll(curr_head_args);
				
				citation_view c = null;
				
				if(valid_tuple.lambda_terms.size() > 0)
				{
					
					c = new citation_view_parametered(valid_tuple.name, valid_tuple.query, valid_tuple);
				}	
				else
				{
					
					c = new citation_view_unparametered(valid_tuple.name, valid_tuple);
				}
				
				for(Iterator iter = curr_view_com.iterator(); iter.hasNext();)
				{
					Covering_set old_view_com = (Covering_set)iter.next();
					
					Covering_set old_view_com_copy = old_view_com.clone(); 
					
					Covering_set view_com = Covering_set.merge(old_view_com_copy, c);
					
					HashSet<String> string_list = new HashSet<String>();
					
					for(citation_view curr_view_mappipng: view_com.c_vec)
					{
						string_list.add(curr_view_mappipng.get_name());
					}
					
//					if(string_list.contains("v4") && string_list.contains("v8") && string_list.contains("v11") && string_list.contains("v6") && string_list.contains("v14") && string_list.contains("v20"))
//					{
//						int y = 0;
//						
//						y++;
//					}
//					if(string_list.contains("v4") && string_list.contains("v8"))
//					{
//						int y = 0;
//						
//						y++;
//					}
					
					remove_duplicate_arg(new_view_com, view_com);
				}
			}
			
			return new_view_com;
		}
	}
	
	
	public static HashSet<Covering_set> join_views_curr_relation(ArrayList<Tuple> tuples, HashSet<Covering_set> curr_view_com, Vector<Argument> curr_head_args, HashMap<String, Query> view_mapping)
    {
        if(curr_view_com.isEmpty())
        {
            if(tuples.isEmpty())
                return new HashSet<Covering_set>();
            else
            {
                HashSet<Covering_set> new_view_com = new HashSet<Covering_set>();
                
                for(int i = 0; i<tuples.size(); i++)
                {
                    
                    Tuple valid_tuple = (Tuple) tuples.get(i).clone();
                    
                    valid_tuple.args.retainAll(curr_head_args);
                    
                    if(valid_tuple.lambda_terms.size() > 0)
                    {
                        
                        citation_view_parametered c = new citation_view_parametered(valid_tuple.name, view_mapping.get(valid_tuple.name), valid_tuple);
                        
                        Covering_set curr_views = new Covering_set(c);
                        
                        remove_duplicate(new_view_com, curr_views);
                    }   
                    else
                    {
                        
                        citation_view_unparametered c = new citation_view_unparametered(valid_tuple.name, valid_tuple);
                        
                        Covering_set curr_views = new Covering_set(c);
                        
                        remove_duplicate(new_view_com, curr_views);
                    }
                }
                
                return new_view_com;
            }
        }
        
        else
        {
            HashSet<Covering_set> new_view_com = new HashSet<Covering_set>();
            
            for(int i = 0; i<tuples.size(); i++)
            {
                Tuple valid_tuple = (Tuple) tuples.get(i).clone();
                
                valid_tuple.args.retainAll(curr_head_args);
                
                citation_view c = null;
                
                if(valid_tuple.lambda_terms.size() > 0)
                {
                    
                    c = new citation_view_parametered(valid_tuple.name, view_mapping.get(valid_tuple.name), valid_tuple);
                }   
                else
                {
                    
                    c = new citation_view_unparametered(valid_tuple.name, valid_tuple);
                }
                
                for(Iterator iter = curr_view_com.iterator(); iter.hasNext();)
                {
                    Covering_set old_view_com = (Covering_set)iter.next();
                    
                    Covering_set old_view_com_copy = old_view_com.clone(); 
                    
                    Covering_set view_com = Covering_set.merge(old_view_com_copy, c);
                    
//                    if(view_com.toString().equals("v17*v23"))
//                    {
//                      System.out.println("tables::" + view_com.table_names);
//                      
//                      System.out.println("args::" + view_com.head_variables);
//                      
//                      System.out.println(view_com.c_vec);
//                    }
//                    
//                    if(view_com.toString().equals("v17*v20*v23*v6"))
//                    {
//                      
//                      System.out.println(old_view_com_copy);
//                      
//                      System.out.println(c);
//                      
//                      System.out.println("tables::" + view_com.table_names);
//                      
//                      System.out.println("args::" + view_com.head_variables);
//                      
//                      System.out.println(view_com.c_vec);
//                    }
                    
                    remove_duplicate(new_view_com, view_com);
                }
            }
            
            return new_view_com;
        }
    }
    
	
	public static HashSet<Covering_set> remove_duplicate(HashSet<Covering_set> c_combinations, Covering_set c_view)
	{
		int i = 0;
		
		if(c_combinations.contains(c_view))
		{
//		  if(c_view.toString().equals("v17*v23"))
//		  {
//		    System.out.println("contained");
//		  }
//		  
//		  for(citation_view_vector view_mapping:c_combinations)
//		  {
//		    if(view_mapping.equals(c_view))
//		    {
//		      System.out.println(view_mapping);
//		      
//		      System.out.println(view_mapping.hashCode());
//		      
//		      System.out.println(c_view.hashCode());
//		    }
//		  }
		  return c_combinations;
		}
				
		for(Iterator iter = c_combinations.iterator(); iter.hasNext();)
		{
						
			Covering_set c_combination = (Covering_set) iter.next();
			
            Covering_set curr_combination = c_view;
            
//            if(c_combination.toString().equals("v1*v4*v5*v6") && curr_combination.toString().equals("v1*v2*v4*v5*v6"))
//            {
//              System.out.println(c_combination.table_names);
//              
//              System.out.println(curr_combination.table_names);
//              
//              System.out.println(c_combination.head_variables);
//              
//              System.out.println(curr_combination.head_variables);
//            }
//            
//            if(curr_combination.toString().equals("v1*v4*v5*v6") && c_combination.toString().equals("v1*v2*v4*v5*v6"))
//            {
//              System.out.println(c_combination.table_names);
//              
//              System.out.println(curr_combination.table_names);
//              
//              System.out.println(c_combination.head_variables);
//              
//              System.out.println(curr_combination.head_variables);
//            }
            
			
			{
				{
					if(c_combination.c_vec.containsAll(curr_combination.c_vec)&& curr_combination.table_names.containsAll(c_combination.table_names) && curr_combination.head_variables.containsAll(c_combination.head_variables) && c_combination.c_vec.size() > curr_combination.c_vec.size())
					{
//					  if(c_combination.toString().equals("v17*v23"))
//			          {
//			            System.out.println("removed");
//			          }
					  
						iter.remove();						
					}
					
					if(curr_combination.c_vec.containsAll(c_combination.c_vec) && c_combination.table_names.containsAll(curr_combination.table_names) && c_combination.head_variables.containsAll(curr_combination.head_variables) && curr_combination.c_vec.size() > c_combination.c_vec.size())
					{
						break;
					}
				}
				
			}
			
			i++;
		}
		
		
		if(i >= c_combinations.size())
		{
//		  if(c_view.toString().equals("v17*v23"))
//		  {
//		    System.out.println("added");
//		  }
		  
		  c_combinations.add(c_view);
		}
		
				
		return c_combinations;
	}
	
	public static HashSet<Covering_set> remove_duplicate_arg(HashSet<Covering_set> c_combinations, Covering_set c_view)
	{
		int i = 0;
		
		if(c_combinations.contains(c_view))
			return c_combinations;
				
		for(Iterator iter = c_combinations.iterator(); iter.hasNext();)
		{
//			String str = (String) iter.next();
						
			Covering_set c_combination = (Covering_set) iter.next();
			{
				{
					Covering_set curr_combination = c_view;
					if(c_combination.c_vec.containsAll(curr_combination.c_vec)&& curr_combination.head_variables.containsAll(c_combination.head_variables) && c_combination.index_vec.size() > curr_combination.index_vec.size())
					{
						iter.remove();						
					}
					
					if(curr_combination.c_vec.containsAll(c_combination.c_vec) && c_combination.head_variables.containsAll(curr_combination.head_variables) && curr_combination.index_vec.size() > c_combination.index_vec.size())
					{
						break;
					}
				}
				
			}
			
			i++;
		}
		
		
		if(i >= c_combinations.size())
			c_combinations.add(c_view);
		
				
		return c_combinations;
	}
	
	public static void get_valid_citation_combination(HashSet<Covering_set> c_view_template, ArrayList<ArrayList<Tuple>>c_unit_vec, Query query, ArrayList<HashSet<Integer>> all_head_vars) throws ClassNotFoundException, SQLException
	{
//		HashSet<citation_view_vector2> c_combinations = new HashSet<citation_view_vector2>();
		
		HashSet<Covering_set> temp_combinations = new HashSet<Covering_set>();
						
//		int valid_subgoal_num = 0;
		
//		for(int i = 0; i<c_unit_vec.size(); i++)
//		{
//			Vector<citation_view> curr_c_unit_vec = c_unit_vec.get(i);
//			
//			Subgoal subgoal = (Subgoal) query.body.get(i); 
//			
//			if(curr_c_unit_vec.size() != 0)
//			{
//				subgoal_names.add(subgoal.name);
////				valid_subgoal_num ++;
//			}
//			
//		}
		
		
//		boolean final_col = false;
		
		for(int i = 0; i<c_unit_vec.size(); i++)
		{
			ArrayList<Tuple> curr_c_unit_vec = c_unit_vec.get(i);
			
			if(curr_c_unit_vec.size() == 0)
				continue;
			
			Subgoal subgoal = (Subgoal) query.body.get(i);
			
			HashSet<Integer> curr_relation_head_vars = all_head_vars.get(i);  
			
			find_max_covering(subgoal.name, curr_relation_head_vars);
			
			if(curr_relation_head_vars.isEmpty())
				continue;
			
			HashSet<Covering_set> view_com = new HashSet<Covering_set>();
			
			for(Iterator iter = curr_relation_head_vars.iterator(); iter.hasNext();)
			{
				
				ArrayList<Tuple> curr_tuples_copy = (ArrayList<Tuple>) curr_c_unit_vec.clone();
				
				Integer curr_head_var = (Integer) iter.next();
				
				ArrayList<Tuple> available_tuples = head_variable_view_mapping[curr_head_var];
				
				curr_tuples_copy.retainAll(available_tuples);
				
				
				
				view_com = join_views_curr_relation(curr_tuples_copy, view_com, curr_relation_head_vars);
				
//				remove_duplicate_view_combinations(view_com);
			}
			
//			remove_duplicate_view_combinations(view_com);
//			else
//			{
//				valid_subgoal_num --;
//				
//				if(valid_subgoal_num == 0)
//					final_col = true;
//			}			
			temp_combinations = join_operation(temp_combinations, view_com, temp_combinations.size());
			
//			remove_duplicate_view_combinations(temp_combinations);
			
			
		}
		
//		remove_duplicate_view_combinations_final(temp_combinations);
		
		c_view_template.addAll(temp_combinations);
		
		temp_combinations.clear();
	}
	
//	public static void get_valid_citation_combination_no_clustering(HashSet<Covering_set> c_view_template, ArrayList<ArrayList<Tuple>>c_unit_vec, Query query, ArrayList<HashSet<Integer>> all_head_vars) throws ClassNotFoundException, SQLException
//    {
//	  HashSet<Covering_set> temp_combinations = new HashSet<Covering_set>();
//      
////    int valid_subgoal_num = 0;
//      
////    for(int i = 0; i<c_unit_vec.size(); i++)
////    {
////        Vector<citation_view> curr_c_unit_vec = c_unit_vec.get(i);
////        
////        Subgoal subgoal = (Subgoal) query.body.get(i); 
////        
////        if(curr_c_unit_vec.size() != 0)
////        {
////            subgoal_names.add(subgoal.name);
//////              valid_subgoal_num ++;
////        }
////        
////    }
//      
//      
////    boolean final_col = false;
//      
//      for(int i = 0; i<c_unit_vec.size(); i++)
//      {
//          ArrayList<Tuple> curr_c_unit_vec = c_unit_vec.get(i);
//          
//          if(curr_c_unit_vec.size() == 0)
//              continue;
//          
//          Subgoal subgoal = (Subgoal) query.body.get(i);
//          
//          HashSet<Integer> curr_relation_head_vars = all_head_vars.get(i);
//          
//          find_max_covering(subgoal.name, curr_relation_head_vars);
//          
//          if(curr_relation_head_vars.isEmpty())
//              continue;
//          
////          HashSet<citation_view_vector> view_com = new HashSet<citation_view_vector>();
//          
//          for(Iterator iter = curr_relation_head_vars.iterator(); iter.hasNext();)
//          {
//              
//              ArrayList<Tuple> curr_tuples_copy = (ArrayList<Tuple>) curr_c_unit_vec.clone();
//              
//              Integer curr_head_var = (Integer) iter.next();
//              
//              ArrayList<Tuple> available_tuples = head_variable_view_mapping[curr_head_var];
//              
//              curr_tuples_copy.retainAll(available_tuples);
//              
//              temp_combinations = join_views_curr_relation(curr_tuples_copy, temp_combinations, query.head.args);
//              
////            remove_duplicate_view_combinations(view_com);
//          }
//          
////        remove_duplicate_view_combinations(view_com);
////        else
////        {
////            valid_subgoal_num --;
////            
////            if(valid_subgoal_num == 0)
////                final_col = true;
////        }           
////          temp_combinations = join_operation(temp_combinations, view_com, temp_combinations.size());
//          
////        remove_duplicate_view_combinations(temp_combinations);
//          
//          
//      }
//      
////    remove_duplicate_view_combinations_final(temp_combinations);
//      
//      c_view_template.addAll(temp_combinations);
//      
//      temp_combinations.clear();
//    }
//	
	static void remove_duplicate_view_combinations(ArrayList<Covering_set> c_view_template)
	{
		for(int i = 0; i<c_view_template.size(); i++)
		{
//			String str = (String) iter.next();
			
//			boolean contains = false;
			
			Covering_set c_combination = c_view_template.get(i);
			

			
			for(int j = 0; j<c_view_template.size(); j++)
			{
//				String string = (String) iterator.next();
				

//				if(str!=string)
				if(i != j)
				{
					Covering_set curr_combination = c_view_template.get(j);
					
					
//					if(c_combination.c_vec.size() == 3)
//					{
//						
//						System.out.println(c_combination);
//						
//						System.out.println(curr_combination);
//						
//						int k = 0;
//						
//						k++;
//					}
					
//					if((c_combination.index_vec.containsAll(curr_combination.index_vec) && c_combination.index_vec.size() > curr_combination.index_vec.size()) || (c_combination.index_vec.equals(curr_combination.index_vec)))
					if((view_vector_contains(c_combination, curr_combination) && table_names_contains(c_combination, curr_combination) && c_combination.index_vec.size() > curr_combination.index_vec.size()))
					{
						
						if(c_combination.table_names.equals(curr_combination.table_names))
						{
							c_view_template.remove(i);
							
							i--;
							
							break;
						}
					}
					
					if(c_combination.index_str.equals(curr_combination.index_str) && c_combination.table_name_str.equals(curr_combination.table_name_str))
					{
						c_view_template.remove(i);
						
						i--;
						
						break;
					}
				}
				
			}
		}
		
	}
	
	static void remove_duplicate_view_combinations_final(ArrayList<Covering_set> c_view_template)
	{
		for(int i = 0; i<c_view_template.size(); i++)
		{
//			String str = (String) iter.next();
			
//			boolean contains = false;
			
			Covering_set c_combination = c_view_template.get(i);
			

			
			for(int j = 0; j<c_view_template.size(); j++)
			{
//				String string = (String) iterator.next();
				

//				if(str!=string)
				if(i != j)
				{
					Covering_set curr_combination = c_view_template.get(j);
					
					
					
					
//					if((c_combination.index_vec.containsAll(curr_combination.index_vec) && c_combination.index_vec.size() > curr_combination.index_vec.size()) || (c_combination.index_vec.equals(curr_combination.index_vec)))
					if((view_vector_contains(c_combination, curr_combination) && table_names_contains(c_combination, curr_combination) && c_combination.c_vec.size() > curr_combination.c_vec.size()))
					{
						
//						if(c_combination.table_names.equals(curr_combination.table_names))
						{
							c_view_template.remove(i);
							
							i--;
							
							break;
						}
					}
				}
				
			}
		}
		
	}
	
	public static Vector<Covering_set> remove_duplicate_final(Vector<Covering_set> c_combinations)
	{
//		Vector<Boolean> retains = new Vector<Boolean>();
		
//		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
				
//		Set set = c_combinations.keySet();
		
//		for(Iterator iter = set.iterator(); iter.hasNext();)
		for(int i = 0; i<c_combinations.size(); i++)
		{
//			String str = (String) iter.next();
						
			Covering_set c_combination = c_combinations.get(i);
			
			
			for(int j = i+1; j<c_combinations.size(); j++)
			{
//				String string = (String) iterator.next();
				
//				if(str!=string)
				if(i != j)
				{
					Covering_set curr_combination = c_combinations.get(j);
					
					if(c_combination.index_str.equals(curr_combination.index_str))
					{
//						if(c_combination.table_names.equals(curr_combination.table_names))
						{
							c_combinations.remove(i);
							
							i--;
							
							break;
						}
					}
				}
				
			}
		}
		
				
		return c_combinations;
	}
	
	public static Vector<Covering_set> remove_duplicate_final2(Vector<Covering_set> c_combinations)
	{
//		Vector<Boolean> retains = new Vector<Boolean>();
		
//		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
		
		for(int i = 0; i<c_combinations.size(); i++)
		{
//			String str = (String) iter.next();
						
			Covering_set c_combination = c_combinations.get(i);
			
			
			for(int j = i+1; j<c_combinations.size(); j++)
			{
//				String string = (String) iterator.next();
				
//				if(str!=string)
				if(i != j)
				{
					Covering_set curr_combination = c_combinations.get(j);
					
					if(c_combination.index_str.equals(curr_combination.index_str))
					{
//						if(c_combination.table_names.equals(curr_combination.table_names))
						{
							c_combinations.remove(i);
							
							i--;
							
							break;
						}
					}
				}
				
			}
		}
		
		for(int i = 0; i<c_combinations.size(); i++)
		{
//			String str = (String) iter.next();
			
//			boolean contains = false;
			
			Covering_set c_combination = c_combinations.get(i);
			
			for(int j = 0; j<c_combinations.size(); j++)
			{
//				String string = (String) iterator.next();
				
//				if(str!=string)
				if(i != j)
				{
					Covering_set curr_combination = c_combinations.get(j);
					
//					if((c_combination.index_vec.containsAll(curr_combination.index_vec) && c_combination.index_vec.size() > curr_combination.index_vec.size()) || (c_combination.index_vec.equals(curr_combination.index_vec)))
					if((view_vector_contains(c_combination, curr_combination) && c_combination.index_vec.size() > curr_combination.index_vec.size()))
					{
						
						
							c_combinations.remove(i);
							
							i--;
							
							break;
					}
					
				}
				
			}
		}
				
//		Set set = c_combinations.keySet();
		
//		for(Iterator iter = set.iterator(); iter.hasNext();)
		
		
				
		return c_combinations;
	}
	
	
	public static Vector<Covering_set> remove_duplicate(Vector<Covering_set> c_combinations)
	{
//		Vector<Boolean> retains = new Vector<Boolean>();
		
//		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
		
//		Vector<citation_view_vector2> update_combinations = new Vector<citation_view_vector2>();
		
//		Set set = c_combinations.keySet();
		
//		for(Iterator iter = set.iterator(); iter.hasNext();)
		for(int i = 0; i<c_combinations.size(); i++)
		{
//			String str = (String) iter.next();
			
//			boolean contains = false;
			
			Covering_set c_combination = c_combinations.get(i);
			
			for(int j = 0; j<c_combinations.size(); j++)
			{
//				String string = (String) iterator.next();
				
//				if(str!=string)
				if(i != j)
				{
					Covering_set curr_combination = c_combinations.get(j);
					
//					if((c_combination.index_vec.containsAll(curr_combination.index_vec) && c_combination.index_vec.size() > curr_combination.index_vec.size()) || (c_combination.index_vec.equals(curr_combination.index_vec)))
					if((view_vector_contains(c_combination, curr_combination)))
					{
						
//						if(c_combination.table_names.equals(curr_combination.table_names))
						{
							c_combinations.remove(i);
							
							i--;
							
							break;
						}
					}
				}
				
			}
		}
		
				
		return c_combinations;
	}
	
	static boolean table_names_contains(Covering_set c_vec1, Covering_set c_vec2)
	{
		String s1 = ".*";
		
		String s2 = c_vec1.table_name_str;
		
		for(citation_view view_mapping: c_vec2.c_vec)
		{
			
			String str = view_mapping.get_table_name_string();
			
			str = str.replaceAll("\\[", "\\\\[");
			
			str = str.replaceAll("\\]", "\\\\]");
			
			s1 += str + ".*";
			
		}
		
		return s2.matches(s1);
	}
	
	static boolean table_names_equals(Covering_set c_vec1, Covering_set c_vec2)
	{
		String s1 = c_vec2.table_name_str;
		
		String s2 = c_vec1.table_name_str;
		
		return s1.equals(s2);
	}
	
	static boolean view_vector_contains(Covering_set c_vec1, Covering_set c_vec2)
	{
		
		String s1 = ".*";
		
		String s2 = c_vec1.index_str;
		
		for(int i = 0; i<c_vec2.index_vec.size(); i++)
		{
			String str = c_vec2.index_vec.get(i);
			
			str = str.replaceAll("\\(", "\\\\(");
			
			str = str.replaceAll("\\)", "\\\\)");
			
			str = str.replaceAll("\\[", "\\\\[");
			
			str = str.replaceAll("\\]", "\\\\]");
			
			str = str.replaceAll("\\/", "\\\\/");
			
			s1 += "\\(" + str + "\\).*";
		}
		
//		if(s2.matches(s1))
//		{
//			int y = 0;
//			
//			y++;
//		}
		
		
		return s2.matches(s1);
		
//		Vector<String> c1 = new Vector<String>();
//		
//		c1.addAll(c_vec1.index_vec);
//		
//		
//		Vector<String> c2 = new Vector<String>();
//		
//		c2.addAll(c_vec2.index_vec);
//		
//		for(int i = 0; i < c2.size(); i++)
//		{
//			int id = c1.indexOf(c2.get(i));
//			
//			if(id >= 0)
//			{
//				c1.remove(id);
//				
//				c2.remove(i);
//				
//				i --;
//			}
//			
//		}
//		
//		if(c2.size() == 0)
//			return true;
//		else
//			return false;
	}
//	
//	public static Vector<citation_view_vector2> remove_duplicate_final(Vector<citation_view_vector2> c_combinations)
//	{
////		Vector<Boolean> retains = new Vector<Boolean>();
//		
////		HashMap<String, Vector<citation_view>> update_combinations = new HashMap<String, Vector<citation_view>>();
//		
//		Vector<citation_view_vector2> update_combinations = new Vector<citation_view_vector2>();
//		
////		Set set = c_combinations.keySet();
//		
////		for(Iterator iter = set.iterator(); iter.hasNext();)
//		for(int i = 0; i<c_combinations.size(); i++)
//		{
////			String str = (String) iter.next();
//			
//			boolean contains = false;
//			
//			citation_view_vector2 c_combination = c_combinations.get(i);
//			
////			for(Iterator iterator = set.iterator(); iterator.hasNext();)
//			for(int j = 0; j<c_combinations.size(); j++)
//			{
////				String string = (String) iterator.next();
//				
//				if(i!=j)
//				{
//					citation_view_vector2 curr_combination = c_combinations.get(j);
//					
//					if(c_combination.index_vec.containsAll(curr_combination.index_vec))
//					{
//						contains = true;
//						
//						break;
//					}
//				}
//				
//			}
//			
//			if(!contains)
//				update_combinations.add(c_combination);
//		}
//		
//				
//		return update_combinations;
//	}
//	
	public static HashSet<Covering_set> join_operation(HashSet<Covering_set> c_combinations, HashSet<Covering_set> insert_citations, int i)
	{
		if(i == 0)
		{
//			for(int k = 0; k<insert_citations.size(); k++)
//			{
//				Vector<citation_view> c = new Vector<citation_view>();
//				
//				c.add(insert_citations.get(k));
//				
////				String str = String.valueOf(insert_citations.get(k).get_index());
//				
//				c_combinations.add(new citation_view_vector2(c));
//				
//			}
			
			c_combinations.addAll(insert_citations);
			
			return c_combinations;
		}
		else
		{
//			HashMap<String, citation_view_vector2> updated_c_combinations = new HashMap<String, citation_view_vector2>();
			
			HashSet<Covering_set> updated_c_combinations = new HashSet<Covering_set>();
			
			
			
//			citation_view_vector2 update_vec = new citation_view_vector2(insert_citations);
//			
//			
//			c_combinations.add(update_vec);
//			
//			return c_combinations;
//			Set set = c_combinations.keySet();
//			
//			for(Iterator iter = set.iterator(); iter.hasNext();)			
			for(Iterator iter = c_combinations.iterator(); iter.hasNext();)
			{
//				String string = (String) iter.next();
				
				Covering_set curr_combination1 = (Covering_set) iter.next();
								
				for(Iterator it = insert_citations.iterator(); it.hasNext();)
				{
					
					Covering_set curr_combination2 = (Covering_set)it.next(); 
					
					Covering_set new_citation_vec = curr_combination2.clone();
					
					Covering_set new_covering_set = curr_combination1.merge(new_citation_vec);
					
					remove_duplicate(updated_c_combinations, new_covering_set);
					
//					boolean contains = false;
//					
//					for(Iterator it2 = updated_c_combinations.iterator(); it2.hasNext();)
//					{
//						citation_view_vector update_covering_set = (citation_view_vector) it2.next();
//						
//						if(update_covering_set.toString().equals("v11*v20*v4*v8"))
//						{
//							contains = true;
//						}
//					}
//					
//					if(!contains)
//					{
//						int y = 0;
//						
//						y++;
//					}
					

				}
			}
						
			return updated_c_combinations;
			
		}
	}
	
	public static void rename_column(Subgoal view, Connection c, PreparedStatement pst) throws SQLException
	{
		String rename_query = "alter table "+view.name +" rename column citation_view to "+ view.name + "_citation_view";
		
		pst = c.prepareStatement(rename_query);
		
		pst.execute();
	}
	
	public static void rename_column_back(Subgoal view, Connection c, PreparedStatement pst) throws SQLException
	{
		String rename_query = "alter table "+view.name +" rename column "+ view.name +"_citation_view to citation_view";
		
		pst = c.prepareStatement(rename_query);
		
		pst.execute();
	}
	
//	public static Vector<Vector<citation_view>> str2c_view(Vector<Vector<String>> citation_str) throws ClassNotFoundException, SQLException
//	{
//		Vector<Vector<citation_view>> c_view = new Vector<Vector<citation_view>>();
//		
//		for(int i = 0; i<citation_str.size(); i++)
//		{
//			Vector<String> curr_c_view_str = citation_str.get(i);
//			
//			Vector<citation_view> curr_c_view = new Vector<citation_view>();
//			
//			for(int j = 0; j<curr_c_view_str.size();j++)
//			{
//				if(citation_str.get(i).contains("(") && citation_str.get(i).contains(")"))
//				{
//					String citation_view_name = curr_c_view_str.get(i).split("\\(")[0];
//					
//					String [] citation_parameters = curr_c_view_str.get(i).split("\\(")[1].split("\\)")[0].split(",");
//					
//					Vector<String> citation_para_vec = new Vector<String>();
//					
//					citation_para_vec.addAll(Arrays.asList(citation_parameters));
//					
//					citation_view_parametered inserted_citation_view = new citation_view_parametered(citation_view_name);
//					
//					inserted_citation_view.put_paramters(citation_para_vec);
//										
//					curr_c_view.add(inserted_citation_view);
//				}
//				else
//				{
//					curr_c_view.add(new citation_view_unparametered(curr_c_view_str.get(i)));
//				}
//			}
//			
//			c_view.add(curr_c_view);
//		}
//		
//		return c_view;
//	}
//	
//	
	public static Vector<String> intersection(Vector<String> c1, Vector<String> c2)
	{
		Vector<String> c = new Vector<String>();
		
		for(int i = 0; i<c1.size(); i++)
		{
			if(c2.contains(c1.get(i)))
				c.add(c1.get(i));
		}
		
		return c;
	}
	
	public static Vector<Vector<String>> get_citation(Subgoal subgoal,Connection c, PreparedStatement pst) throws SQLException, ClassNotFoundException
	{
		Vector<Vector<String>> c_view = new Vector<Vector<String>>();
		
		String table_name = subgoal.name + "_c";
		
		String schema_query = "select column_name from INFORMATION_SCHEMA.COLUMNS where table_name = '" + table_name + "'";
		
		
		pst = c.prepareStatement(schema_query);
		
		ResultSet rs = pst.executeQuery();
		
		int num = 0;
		
		boolean constant_variable = false;
		
		String citation_view_query = "select citation_view from " + table_name;
		
		while(rs.next())
		{
			String column_name = rs.getString(1);
			
			Argument arg = (Argument) subgoal.args.get(num++);
			
			if(arg.type == 1)
			{
				if(constant_variable == false)
				{
					constant_variable = true;
					
					citation_view_query += " where ";
				}
				
				citation_view_query += column_name + "=" + arg.name;
			}
			
			if(num >= subgoal.args.size())
				break;
		}
		
		pst = c.prepareStatement(citation_view_query);
		
		rs = pst.executeQuery();
		
		while(rs.next())
		{
			String[] citation_str = rs.getString(1).split("\\"+populate_db.separator);
			
			
			
			Vector<String> curr_c_view = new Vector<String>();
			
			curr_c_view.addAll(Arrays.asList(citation_str));
			
			c_view.add(curr_c_view);
		}
		
		return c_view;
	}
	
//	public static Vector<citation_view> str2c_unit(String [] citation_str) throws ClassNotFoundException, SQLException
//	{
//		Vector<citation_view> c_view = new Vector<citation_view>();
//			
//			for(int j = 0; j<citation_str.length;j++)
//			{
//				if(citation_str[j].contains("(") && citation_str[j].contains(")"))
//				{
//					String citation_view_name = citation_str[j].split("\\(")[0];
//					
//					String [] citation_parameters = citation_str[j].split("\\(")[1].split("\\)")[0].split(",");
//					
//					Vector<String> citation_para_vec = new Vector<String>();
//					
//					citation_para_vec.addAll(Arrays.asList(citation_parameters));
//					
//					citation_view_parametered inserted_citation_view = new citation_view_parametered(citation_view_name);
//					
//					inserted_citation_view.put_paramters(citation_para_vec);
//										
//					c_view.add(inserted_citation_view);
//				}
//				else
//				{
//					c_view.add(new citation_view_unparametered(citation_str[j]));
//				}
//			}
//		
//		return c_view;
//	}
	

//    static Query parse_query(String query)
//    {
//        
//        String head = query.split(":")[0];
//        
//        String head_name=head.split("\\(")[0];
//        
//        String []head_var=head.split("\\(")[1].split("\\)")[0].split(",");
//        
//        Vector<Argument> head_v = new Vector<Argument>();
//        
//        for(int i=0;i<head_var.length;i++)
//        {
//        	head_v.add(new Argument(head_var[i]));
//        }
//        
//        Subgoal head_subgoal = new Subgoal(head_name, head_v);
//        
//        String []body = query.split(":")[1].split("\\),");
//        
//        body[body.length-1]=body[body.length-1].split("\\)")[0];
//        
//        Vector<Subgoal> body_subgoals = new Vector<Subgoal>(body.length);
//        
//        for(int i=0; i<body.length; i++)
//        {
//        	String body_name=body[i].split("\\(")[0];
//            
//            String []body_var=body[i].split("\\(")[1].split(",");
//            
//            Vector<Argument> body_v = new Vector<Argument>();
//            
//            for(int j=0;j<body_var.length;j++)
//            {
//            	body_v.add(new Argument(body_var[j]));
//            }
//            
//            Subgoal body_subgoal = new Subgoal(body_name, body_v);
//            
//            body_subgoals.add(body_subgoal);
//        }
//        return new Query(head_name, head_subgoal, body_subgoals);
//    }
//    
//    static Query parse_query(String head, String []body, String []lambda_term_str)
//    {
//                
//        String head_name=head.split("\\(")[0];
//        
//        String []head_var=head.split("\\(")[1].split("\\)")[0].split(",");
//        
//        Vector<Argument> head_v = new Vector<Argument>();
//        
//        for(int i=0;i<head_var.length;i++)
//        {
//        	head_v.add(new Argument(head_var[i]));
//        }
//        
//        Subgoal head_subgoal = new Subgoal(head_name, head_v);
//        
////        String []body = query.split(":")[1].split("\\),");
//        
//        body[body.length-1]=body[body.length-1].split("\\)")[0];
//        
//        Vector<Subgoal> body_subgoals = new Vector<Subgoal>(body.length);
//        
//        for(int i=0; i<body.length; i++)
//        {
//        	String body_name=body[i].split("\\(")[0];
//            
//            String []body_var=body[i].split("\\(")[1].split(",");
//            
//            Vector<Argument> body_v = new Vector<Argument>();
//            
//            for(int j=0;j<body_var.length;j++)
//            {
//            	body_v.add(new Argument(body_var[j]));
//            }
//            
//            Subgoal body_subgoal = new Subgoal(body_name, body_v);
//            
//            body_subgoals.add(body_subgoal);
//        }
//        
//        
//        Vector<Argument> lambda_terms = new Vector<Argument>();
//        
//        if(lambda_term_str != null)
//        {
//	        for(int i =0;i<lambda_term_str.length; i++)
//	        {
//	        	lambda_terms.add(new Argument(lambda_term_str[i]));
//	        }
//        
//        }
//        return new Query(head_name, head_subgoal, body_subgoals,lambda_terms);
//    }


}
