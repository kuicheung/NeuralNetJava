package com.kinectnnet;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;

import org.encog.util.file.FileUtil;

import com.google.common.primitives.Doubles;

public class DTW {
   
	public static TreeMap<String,Double[]> loadScores() throws IOException, ClassNotFoundException {
		File masterFolder = new File("C:/Users/Kiwi/Documents/CS156/TeamProject/convertedDataMaster/second_Steven-s2_u0");
		String[] masterFiles = masterFolder.list();
		
		PrintWriter writer = new PrintWriter("C:/Users/Kiwi/Documents/CS156/TeamProject/javaConvertedScores.txt", "UTF-8");
		
		TreeMap<String,Double[]> masterArrays = new TreeMap<String,Double[]>();
		for(String file : masterFiles){
			masterArrays.put(file, readFile(masterFolder+"/"+file));
			writer.write(file+",");
		}
		writer.write("\n");
		
		File dataPath = new File("C:/Users/Kiwi/Documents/CS156/TeamProject/convertedData2");
		String[] directories = dataPath.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File dir, String name) {
		    return new File(dir, name).isDirectory();
		  }
		});
		
		
		TreeMap<String,Double[]> scores = new TreeMap<String,Double[]>();
		for(String dir : directories){
			TreeMap<String,Double[]> profileArrays = new TreeMap<String,Double[]>();
			String[] files = new File(dataPath+"/"+dir).list();
			Double[] jointScores = new Double[120];
			for(int i=0;i<files.length;i++){
				System.out.print(files[i]);
				Double[] jointAry = readFile(dataPath+"/"+dir+"/"+files[i]);
				jointScores[i] = DTWDistance(jointAry,masterArrays.get(files[i]));
				profileArrays.put(files[i], jointAry );
			}
			scores.put(dir, jointScores);
			System.out.println();
		}
		
		
		for(String key : scores.keySet()){
			writer.write(key+",");
			for(Double d : scores.get(key))
				writer.write(d+",");
			writer.write("\n");
		}
		writer.close();
		
		return scores;
	}
	
   private static Double[] readFile(String filename) throws IOException{
	   String input = FileUtil.readFileAsString(new File(filename)).trim();

	   String[] str = input.split(" ");
	   Double[] ary = new Double[str.length];
	   
	   for(int i=0;i<str.length;i++)
		   ary[i] = Double.parseDouble(str[i]);
	   
	   return ary;
   }
   
   public static TreeMap<Integer,Double[]> scoreJoints(Joint[] joints,Joint[] masterJoints){
	   TreeMap<Integer,Double[]> scores = new TreeMap<Integer,Double[]>();
	  
	   for(int i=0;i<joints.length;i++){
		   JointConverted jointC = new JointConverted(masterJoints[i]);
		   JointConverted masterJointC = new JointConverted(joints[i]);
		   Double[] jointScores = new Double[6];
		   jointScores[0] = DTWDistance(Doubles.toArray(jointC.rot1),
					Doubles.toArray(masterJointC.rot1));
		   jointScores[1] = DTWDistance(Doubles.toArray(jointC.rot2),
					Doubles.toArray(masterJointC.rot2));
		   jointScores[2] = DTWDistance(Doubles.toArray(jointC.rot3),
					Doubles.toArray(masterJointC.rot3));
		   jointScores[3] = DTWDistance(Doubles.toArray(jointC.posX),
				   						Doubles.toArray(masterJointC.posX));
		   jointScores[4] = DTWDistance(Doubles.toArray(jointC.posY),
						Doubles.toArray(masterJointC.posY));
		   jointScores[5] = DTWDistance(Doubles.toArray(jointC.posZ),
						Doubles.toArray(masterJointC.posZ));		   
		   scores.put(joints[i].jointNum,jointScores);
	   }
	   return scores;
   }
   
private static double DTWDistance(double[] a1, double[] a2){
	   
	   
	   int n = a1.length;
	   int m = a2.length;
	   
	   Double[][] dtw = new Double[n][m];

	   for(int i=0;i<n;i++)
		   dtw[i][0] = 100000000.0;
	   for(int i=0;i<m;i++)
		   dtw[0][i] = 100000000.0;
	   dtw[0][0] = 0.0;

	   Double cost = 0.0;
	   for(int i=1;i<n;i++){
		   for(int j=1;j<m;j++){
			   cost = Math.abs(a1[i]-a2[j]);
			   dtw[i][j] = cost + min(dtw[i-1][j],
					   				dtw[i][j-1],
					   				dtw[i-1][j-1]);
		   }
	   }
	   
	   return dtw[n-1][m-1];

	}
   
   private static double DTWDistance(Double[] a1, Double[] a2){
	   
	   
	   int n = a1.length;
	   int m = a2.length;
	   
	   Double[][] dtw = new Double[n][m];

	   for(int i=0;i<n;i++)
		   dtw[i][0] = 100000000.0;
	   for(int i=0;i<m;i++)
		   dtw[0][i] = 100000000.0;
	   dtw[0][0] = 0.0;

	   Double cost = 0.0;
	   for(int i=1;i<n;i++){
		   for(int j=1;j<m;j++){
			   cost = Math.abs(a1[i]-a2[j]);
			   dtw[i][j] = cost + min(dtw[i-1][j],
					   				dtw[i][j-1],
					   				dtw[i-1][j-1]);
		   }
	   }
	   
	   return dtw[n-1][m-1];

	}
   
   private static double min(double d1,double d2,double d3){
	   double min = d1;
	   if(d2<min)
		   min = d2;
	   if(d3<min)
		   min = d3;
	   
	   return min;
	   
   }
	   

}
