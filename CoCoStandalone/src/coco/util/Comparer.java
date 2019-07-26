package coco.util;

import java.util.ArrayList;

public class Comparer {

	public Comparer() {
		
		
		super();
		// TODO Auto-generated constructor stub
	}
	public void Compare() {
		
	}
	public String[] EvaluateSim(String obj1, String obj2) {
		ArrayList res = new ArrayList();
		String []arr1 = obj1.split("\n");
		String []arr2 = obj2.split("\n");
		int is = 0;
				
		for(int i = 0;i< arr1.length;i++) {
			for(int j = 0;j< arr2.length;j++) {
				if(arr1[i].equals(arr2[j])) {
					is++;
					res.add(i+"-"+j);
				}
			}
		}
		double sim_1 = is/arr1.length;
		double sim_2 = is/arr2.length;
		System.out.println(sim_1+"_____"+sim_2);
		String[] resdef = new String[res.size()];
		for(int i =0;i< res.size();i++) {
			resdef[i]=(String) res.get(i);
			System.out.println(resdef[i]);
		}
		return resdef;
	}
	public void Prepare(String z3, String choco) {
		int solsz3 = 0;
		int index_solsz3 = -1;
		int compsz3 = 0;
		int index_compsz3 = -1;
		String[] z3tmp1 = z3.split("\n");
		String [][][] z3data = new String[0][0][0];
		for(int i = 0;i<z3tmp1.length;i++) {
			String [] tmp2 = z3tmp1[i].split(":");
			if(tmp2[0].equals("SOLUCIONES")) {
				solsz3 = Integer.parseInt(tmp2[1]);
				compsz3 = 0;
			}else if(tmp2[0].equals("COMPONENTES")) {
				compsz3 = Integer.parseInt(tmp2[1]);
				z3data = new String[solsz3][compsz3][2];
				
			}
			else if(tmp2[0].equals("PUNTO_DE_DIVISION_SALIDA")){
				index_solsz3++;
				index_compsz3 = 0;
			}else {
				
				z3data[index_solsz3][index_compsz3][0]=tmp2[0]+"";
				z3data[index_solsz3][index_compsz3][1]=tmp2[1]+"";
				index_compsz3++;
			}
			if(solsz3 == 0) {
				System.out.println("ERRORRRR");
			}
			
		}
		int solschoco = 0;
		int index_solschoco = -1;
		int compschoco = 0;
		int index_compschoco = -1;
		String[] chocotmp1 = choco.split("\n");
		String [][][] chocodata = new String[0][0][0];
		for(int i = 0;i<chocotmp1.length;i++) {
			String [] tmp2 = chocotmp1[i].split(":");
			if(tmp2[0].equals("SOLUCIONES")) {
				solschoco = Integer.parseInt(tmp2[1]);
				compschoco = 0;
			}else if(tmp2[0].equals("COMPONENTES")) {
				compschoco = Integer.parseInt(tmp2[1]);

				chocodata = new String[solschoco][compschoco][2];
				
			}
			else if(tmp2[0].equals("PUNTO_DE_DIVISION_SALIDA")){
				index_solschoco++;
				index_compschoco = 0;
			}else {
				
				chocodata[index_solschoco][index_compschoco][0]=tmp2[0]+"";
				chocodata[index_solschoco][index_compschoco][1]=tmp2[1]+"";
				index_compschoco++;
			}
			if(solschoco == 0) {
				System.out.println("ERRORRRR1");
			}
			
		}
		int comps = 0;
		
		int sols = 0;
		String [][][] maytemp = chocodata ;
		String [][][] mentemp = z3data;
		
		if(compschoco == compsz3) {
			comps = compsz3;
			
		}else {
			System.out.println("choco:"+compschoco+":"+chocodata[0].length+"   z3:"+compsz3+":"+z3data[0].length);
			maytemp = (compschoco > compsz3)?chocodata:z3data;
			mentemp = (compschoco > compsz3)?z3data:chocodata;
			
			for(int w = 0; w < maytemp.length;w++) {
				String x = maytemp[0][w][0];
				boolean x1 = false;
				for(int z = 0; z < mentemp.length;z++) {
					String y = mentemp[0][z][0];
					if(y.equals(x)) {
						x1 = true;
						
						break;
						
					}
				}
				if(!x1)System.out.println("noesta:"+x);
			}
			System.out.println("ERROOOORRRR2:"+compschoco+"__"+compsz3);
		}
		if(solschoco == solsz3) {
			sols = solschoco;
		}else {
			sols = (solschoco > solsz3)?solsz3:solschoco;
			maytemp = (solschoco > solsz3)?chocodata:z3data;
			mentemp = (solschoco > solsz3)?z3data:chocodata;
		}
		System.out.println("#soluciones choco:"+solschoco+"    #soluciones z3:"+solsz3);
		double [][][] similitud = new double [sols][2][2];
		int similitud_temp = 0;
		for(int i =0;i<mentemp.length;i++) {
			similitud[i][0][0]=0;
			similitud[i][1][1]=-1;
			for(int x =0;x<maytemp.length;x++) {
				
				
			for(int j =0;j<mentemp[i].length;j++) {
				String key = mentemp[i][j][0];
				String value = mentemp[i][j][1];
				

					for(int y =0;y<maytemp[x].length;y++) {
						String key1 = maytemp[x][y][0];
						String value1 = maytemp[x][y][1];

						if(key.equals(key1) && value.equals(value1)) {
							similitud_temp++;
						}
								
					}
				}
			if (similitud_temp > similitud[i][0][0]) {
				similitud[i][0][0] = similitud_temp;
				similitud[i][1][0] = x;
				similitud[i][1][1] = i;
			}
			similitud_temp = 0;
			} 
		}
		
		for(int i=0;i<similitud.length;i++) {
			double comps1 = comps;
			similitud[i][0][0]=(similitud[i][0][0]/comps1)*100;
			System.out.println("porcentaje similitud sol:"+similitud[i][0][0]+" entre sol "+(similitud[i][0][1]+1)+" y sol "+(similitud[i][1][1]+1));
		
		}
	}

}
