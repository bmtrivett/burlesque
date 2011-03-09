package Loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import Assembler.Tables;
import Assembler.Utility;



public class LinkerLoader {
	 
		public static String output(String[] names) throws IOException {
			//Receive the String array from the assemble with all the file names
			//Remove the first name
			String firstFile = names[0];
			String read = "";
			String end = "";
			//read from the first file inputed
			FileReader reader = new FileReader(firstFile);
			BufferedReader file = new BufferedReader(reader);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
			"file.txt"));
			
			//read in the first line
			read = file.readLine();
			
			String header = overSubstring(read,0, 11);
			
			if(header.charAt(0) == 'G')
			{
				header = 'H' + header.substring(1);
			}
			
		
			//it made me change the modifier to static?
			header = header.concat(Utility.DecimalValueToHex(Tables.locationCounter));
			//write it to the new file
			bufferedWriter.write(header);
			bufferedWriter.newLine();
			//read the next line
			read = file.readLine();
			while(read.charAt(0) != 'E')
			{
				//check to see if it is a P and change it to a T
				if(read.charAt(0) == 'P')
				{
					read = 'T' + read.substring(1);
				}
				//write it to the new file
				bufferedWriter.write(read);
				bufferedWriter.newLine();
				
				//read in the next line
				read = file.readLine();
			}
			if(read.charAt(0) == 'E')
			{
				end = read;
			}
			
			int count = 1;
			//while there are more than 1 file keep reading them
			while(names.length > count)
			{
				//read from the next file
				reader = new FileReader(names[count]);
				file = new BufferedReader(reader);
				//read the first line and do nothing
				read = file.readLine();
				//read the first text record
				read = file.readLine();
				while(read.charAt(0) != 'E')
				{
					//check to see if it is a P and change it to a T
					if(read.charAt(0) == 'P')
					{
						read = 'T' + read.substring(1);
					}
					//write it to the new file
					bufferedWriter.write(read);
					bufferedWriter.newLine();
					
					//read in the next line
					read = file.readLine();
					
				}
				if(read.charAt(0) == 'E')
				{
					//move on to the next file
					read = file.readLine();
					count++;
				}	
				
			}
			
			
			//write the end record
			bufferedWriter.write(end);
			bufferedWriter.newLine();
			return null;
		}
		private static String overSubstring(String str, int x, int y) {
			Boolean exceptions = true;
			int z = 0;
			while (exceptions && x != y) {
				exceptions = false;
				try {
					str.substring(x, y);
				} catch (Exception e) {
					exceptions = true;
					y--;
					z++;
				}
			}
			String temp = str.substring(x, y);
			while (z > 0) {
				temp = temp + " ";
				z--;
			}
			return temp;
		}
}