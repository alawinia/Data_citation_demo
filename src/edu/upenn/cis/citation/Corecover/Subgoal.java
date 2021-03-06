package edu.upenn.cis.citation.Corecover;
/* 
 * Subgoal.java
 * -------------
 * $Id: Subgoal.java,v 1.14 2000/11/25 02:34:37 chenli Exp $
 */

import java.util.*;

public class Subgoal {
  public String name = null;  // subgoal name
  public Vector args = null;  // a list of arguments
  public Vector<Vector<Argument>> agg_args = null;
  public Vector agg_function = null;
  public boolean has_agg = false;
//  public String origin_name = null;

  public Subgoal(String name, Vector args) {
    this.name = name;
    this.args = args;
  }
  
  public Subgoal(String name, Vector args, Vector<Vector<Argument>> agg_args, Vector agg_function, boolean has_agg) {
    this.name = name;
    this.args = args;
    this.agg_args = agg_args;
    
    this.agg_function = agg_function;
    
    this.has_agg = has_agg;
  }
  
//  public Subgoal(String name, Vector args) {
//	    this.name = name;
//	    this.args = args;
////	    this.origin_name = origin_name;
//	  }

  Subgoal(Vector relations, int relIndex, Vector argDom) {

    args = new Vector();  // a list of variables    
    Relation relation = ((Relation) relations.elementAt(relIndex));
    name = relation.getName();
    int argNum = relation.getAttrNum();
    for (int i = 0; i < argNum; i ++) {
      // randomly picks one argument
      int argIndex = GoodPlan.random.nextInt(argDom.size());
      Argument arg = (Argument) argDom.elementAt(argIndex);
      args.add(arg);
    }
  }

  /**
   * Checks if two subgoals have the same name
   */
  public boolean isSameName(Subgoal subgoal) {
    return this.getName().equalsIgnoreCase(subgoal.getName());
  }

  /**
   * renames the arguments in this subgoal given a new id Notice
   * after this steps, two subgoals can share the same argument
   * which is stored in two argument objects.  returns a new subgoal.
   */
  public Subgoal rename(int newId, Map argMap) {

    Vector newArgs = new Vector();
    for (int i = 0; i < args.size(); i ++) {
      Argument oldArg = (Argument) args.elementAt(i);
      Argument newArg = oldArg.rename(newId, argMap);
      if (!argMap.containsKey(oldArg))
	argMap.put(oldArg, newArg);
      
      newArgs.add(newArg);
    }
    
    return new Subgoal(this.getName(), newArgs);
  }
  
  public Vector getArgs() {
    return args;
  }

  public String getName() {
    return name;
  }

  public int size() {
    return args.size();
  }

  public boolean contains(Argument arg) {
    return args.contains(arg);
  }
  
  public boolean contains(Vector<Argument> arg) {
	  
	  
	  boolean containing = false;
	  for(int i = 0; i<arg.size(); i++)
	    {
		  if(args.contains(arg.get(i)))
		  {
	    	containing = true;
	    	break;
		  }
	    }
	  return containing;
	  }

  /**
   * given a partial mapping, tests if this subgoal can be mapped to
   * another mapped by extending the subgoal.
   */
  public boolean unifiable(Subgoal subgoal, Mapping cm, boolean checkName) {

    if (checkName && !isSameName(subgoal))
      return false;

    Vector srcArgs = this.getArgs();
    Vector dstArgs = subgoal.getArgs();
    if (srcArgs.size() != dstArgs.size()) 
      UserLib.myerror("Subgoal.unifiable(), wrong args!");
    
    // checks if a src arg is unifiable with the corresponding dst arg
    for (int i = 0; i < srcArgs.size(); i ++) {
      Argument srcArg = (Argument) srcArgs.elementAt(i);
      Argument dstArg = (Argument) dstArgs.elementAt(i);

      if (srcArg.isConst()) {  // constant -> same constant
	if (dstArg.isConst() && dstArg.equals(srcArg)) {
	  cm.put(srcArg, dstArg);  // const -> const
	  continue;
	}
	return false;
      }

      Argument image = cm.apply(srcArg);
      if (image == null) {
	cm.put(srcArg, dstArg);  // var -> var/const
      }
      else {
	if (!image.equals(dstArg)) 
	  return false;
      }
    }

    return true;
  }

  public String toString() {
    StringBuffer result = new StringBuffer(name);
    result.append("(");
    for (int i = 0; i < args.size(); i ++) {
      result.append(args.elementAt(i).toString());
      if ( i < args.size() - 1)
	result.append(",");
    }
    
    
    if(agg_args != null)
    {
      for(int i = 0; i<agg_args.size(); i++)
      {
        if(args.size() > 0 || (args.size() == 0 && i >= 1))
          result.append(",");
        
        result.append(agg_function.get(i) + "(" + agg_args.get(i).toString() + ")");
      }
    }
    
    result.append(")");
    
    return result.toString();
  }
  
  @Override
  public boolean equals(Object o)
  {
	  Subgoal subgoal = (Subgoal) o;
	  
	  if(subgoal.name.equals(this.name))
		  return true;
	  
	  return false;
  }
  
  @Override
  public int hashCode()
  {
	  return this.toString().hashCode();
  }
}
