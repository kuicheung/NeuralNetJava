package com.kinectnnet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import org.encog.util.file.FileUtil;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Profile {
	private static TreeMap<String,Profile> profiles = new TreeMap<String,Profile>();
	private Joint[] joints;
	private TreeMap<Integer,Double[]> dtwScores;
	private double judgeScore;
	
	public Profile(TreeMap<Integer,Double[]> dtwScores, Joint[] joints,double judgeScore){
		this.dtwScores = dtwScores;
		this.joints = joints;
		this.judgeScore = judgeScore;
	}
	
	public static void loadProfiles() throws IOException{
		String[] filenames = new File("profiles").list();
		
		JsonParser parser = new JsonParser();
		KinectParser kinectParser = new KinectParser();
		
		String masterData = FileUtil.readFileAsString(new File("second_Steven-s2_u0.txt"));	        
        Joint[] masterJoints = kinectParser
        			.readKinectData(parser.parse(masterData).getAsJsonObject());
        
        String judgeScoresStr = FileUtil.readFileAsString(new File("judgeScoreWName.csv"));	
        HashMap<String,Double> judgeScoreMap = new HashMap<String, Double>();
        for(String row : judgeScoresStr.split("\n")){
        	String[] cells = row.split(",");
        	judgeScoreMap.put(cells[0].trim(),Double.parseDouble(cells[1]));
        }
        
		for(String filename: filenames){
			String fileContent = FileUtil.readFileAsString(new File("profiles/"+filename));

			JsonObject profile = parser.parse(fileContent).getAsJsonObject();
			Joint[] joints = kinectParser.readKinectData(profile);
			TreeMap<Integer,Double[]> dtwScores = DTW.scoreJoints(joints, masterJoints);
			profiles.put(filename, new Profile(dtwScores,joints,judgeScoreMap.get(filename)));
		}
	}
	
	public static TreeMap<String,Profile> getProfiles(){
		return profiles;
	}
	
	public TreeMap<Integer,Double[]> getDTWScores(){
		return dtwScores;
	}
	
	public Joint[] getJoints(){
		return joints;
	}
	
	public double getJudgeScore(){
		return judgeScore;
	}

}
