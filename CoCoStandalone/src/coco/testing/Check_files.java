package coco.testing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import coco.util.Util;

public class Check_files {
	public static void main(String[] args) {

	}

	public List files() {
		List<Object[]> obj = new ArrayList<>();

		//int[] feats = { 10, 15, 20 };

		//for (int k = 0; k < feats.length; k++) {

			//File f = new File(Util.PATH_PROPERTIES + "/" + feats[k]);
			File f = new File(Util.PATH_PROPERTIES);
			File[] subFiles = f.listFiles();
			int[] heuristicas = { 0, 1, 2, 3 };
			for(int x = 1; x < 2; x++) {
			for (int i = 0; i < subFiles.length; i++) {
				if (subFiles[i].getName().contains(".properties")) {
					//System.out.println(subFiles[i].getName());
					for (int j = 0; j < heuristicas.length; j++) {
						//System.out.println(subFiles[i].getName() + "___" + heuristicas[j] + "___" + feats[k]);

						obj.add(new Object[] { subFiles[i].getName(), heuristicas[j], x});
					}
				}
			}
			}

		//}
		System.out.println(
				"sols;heuristica;nombre;features;combs;Variables;Constraints;Solution;Build time;Resolution;Nodes;Backtracks;Fails");

		return obj;
	}
}
