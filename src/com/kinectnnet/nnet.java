package com.kinectnnet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.encog.Encog;


/*
 * This is used for generating and training neural nets from prepared
 * CSV files or plain motion profiles from Kinect.
 */
public class nnet {
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		//The location of where to load and save the networks with minimal error
		String networksFilename = "c:/tomcat/webapps/nnet/webcontent/data/networks.ser";
	
		ArrayList<Network> networks = buildNetworksFromSources();
		ArrayList<Network> minNetworks = Network.loadNetworks(networksFilename);

		minNetworks = runTrain(networks,minNetworks);

		for(Network network : minNetworks){
			network.test();	
		}
		
		Network.saveNetworks(networksFilename,minNetworks);
		
		Encog.getInstance().shutdown();

	}
	
	/*
	 * Train neural nets multiple times with random weights and save
	 * the networks with minimal error for Web App  
	 */
	private static ArrayList<Network> runTrain(ArrayList<Network> networks,ArrayList<Network> minNetworks) throws IOException{
		if(minNetworks==null){
			minNetworks=new ArrayList<Network>();
	
			for(int i=0;i<networks.size();i++){
				networks.get(i).train();
				minNetworks.add(networks.get(i));
			}
		}
		
		for(int ct=0;ct<10;ct++){
			networks = buildNetworksFromSources();
			for(int i=0;i<networks.size();i++){
				Network network = networks.get(i);
				network.train();
				double error = network.test();	
				Network minNetwork = minNetworks.get(i);
				if(error<minNetwork.getTestError())
					minNetworks.set(i, new Network(network));
			}
		}
		return minNetworks;
	}
	
	/*
	 * Build Encog Neural Nets bases on filenames
	 */
	private static ArrayList<Network> buildNetworksFromFiles() throws IOException{
		ArrayList<Network> networks =new ArrayList<Network>();
		networks.add(new Network("WristAlignmentWithChest.csv",18,1,new int[]{12,2,3}));
		networks.add(new Network("WristElbowShoulderAlignment.csv",18,1,new int[]{12,13,14}));
		networks.add(new Network("OppositionRecoil.csv",18,1,new int[]{6,2,3}));
		networks.add(new Network("hipinititation.csv",18,1,new int[]{17,18,19}));
		networks.add(new Network("SpiralTransfer.csv",18,1,new int[]{1,2,3}));
		networks.add(new Network("ShouldersDown.csv",12,1,new int[]{12,6}));
		return networks;
	}
	
	/*
	 * Build Encog Neural Nets bases on files in profiles folder
	 */
	private static ArrayList<Network> buildNetworksFromSources() throws IOException{
		Profile.loadProfiles();
		TreeMap<String,Profile> profiles = Profile.getProfiles();
		
		ArrayList<DataSource> sources = new ArrayList<DataSource>();
		sources.add(new DataSource(profiles, 48, 1,new int[]{17,18,19,20,21,22,23,24}));
		sources.add(new DataSource(profiles, 30, 1,new int[]{1,2,3,6,12}));
		sources.add(new DataSource(profiles, 84, 1,new int[]{2,3,7,8,9,12,17,18,19,20,21,22,23,24}));
		sources.add(new DataSource(profiles, 24, 1,new int[]{12,13,14,15}));
		sources.add(new DataSource(profiles, 24, 1,new int[]{2,3,12,13}));
		sources.add(new DataSource(profiles, 12, 1,new int[]{6,12}));
		
		ArrayList<Network> networks =new ArrayList<Network>();
		networks.add(new Network("HipInititation",sources.get(0)));
		networks.add(new Network("SpiralTransfer",sources.get(1)));
		networks.add(new Network("OppositionRecoil",sources.get(2)));
		networks.add(new Network("Wrist-Elbow-ShoulderAlignment",sources.get(3)));
		networks.add(new Network("Wristalignmentwithchest",sources.get(4)));
		networks.add(new Network("ShouldersDown",sources.get(5)));
		
		return networks;
	}


}
