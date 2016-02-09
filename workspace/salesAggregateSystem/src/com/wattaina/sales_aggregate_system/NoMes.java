//package com.wattaina.sales_aggregate_system;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map.Entry;
//
//public class NoMes {
//	public static void main(String[] args) {
//
//		// コマンドライン引数の中身があるかどうか
//		if (args.length == 0) {
//			System.out
//					.println("集計システムに必要なファイルがあるディレクトリのパスを、コマンドプロンプトから入力してください");
//			return;
//		}
//		// 支店定義ファイルの読込
//		HashMap<String, String> branchList = new HashMap<String, String>();
//		HashMap<String, Long> salesBranch = new HashMap<String, Long>();
//		BufferedReader br = null;
//		String record = null;
//		try {
//			br = new BufferedReader(new FileReader(new File(args[0],
//					"branch.lst")));
//			// ファイルを一行ずつ読込む
//			while ((record = br.readLine()) != null) {
//				// 読み込んだデータをカンマで分割
//				String[] splittedReadLine = record.split(",", -1);
//				if (splittedReadLine.length != 2) {
//					System.out.println("支店定義ファイルのフォーマットが不正です");
//					return;
//				}
//				// 分割したデータを、それぞれ「支店コード」と「支店名」としデータを保持する
//				String branchCode = splittedReadLine[0];
//				String branchName = splittedReadLine[1];
//				// 支店コードが数字3桁でなければエラー処理
//				if (!branchCode.matches("^\\d{3}")) {
//					System.out.println("支店定義ファイルのフォーマットが不正です");
//					return;
//				}
//				// 支店コードをキーとした支店名を支店定義用HashMapに追加
//				branchList.put(branchCode, branchName);
//				// 支店コードをキーとした売上金額(初期値として0)を支店別合計売上額HashMapに追加
//				salesBranch.put(branchCode, 0L);
//			}
//		} catch (FileNotFoundException e) {
//			System.out.println("支店定義ファイルが存在しません");
//			return;
//		} catch (IOException e) {
//			System.out.println("ファイル入出力の際にエラーが発生しました。");
//			return;
//		} finally {
//			try {
//				br.close();
//			} catch (IOException e) {
//				System.out.println("ファイル入出力の際にエラーが発生しました。");
//				return;
//			}
//		}
//		// 商品定義ファイルの読込
//		HashMap<String, String> commodityList = new HashMap<String, String>();
//		HashMap<String, Long> salesCommodity = new HashMap<String, Long>();
//		try {
//			br = new BufferedReader(new FileReader(new File(args[0],
//					"commodity.lst")));
//			// ファイルを一行ずつ読込む
//			while ((record = br.readLine()) != null) {
//				// 見込んだデータをカンマで分割
//				String[] splittedReadLine = record.split(",", -1);
//				if (splittedReadLine.length != 2) {
//					System.out.println("商品定義ファイルのフォーマットが不正です");
//					return;
//				}
//				// 分割したデータを、それぞれ「商品コード」と「商品名」としデータを保持する
//				String commodityCode = splittedReadLine[0];
//				String commodityName = splittedReadLine[1];
//				// 商品コードが数字商品コードがアルファベットと数字のみの8桁でなければエラー処理
//				if (!commodityCode.matches("[a-zA-Z0-9]{8}")) {
//					System.out.println("商品定義ファイルのフォーマットが不正です");
//					return;
//				}
//				// 商品コードをキーとした商品名を支店定義用HashMapに追加
//				commodityList.put(commodityCode, commodityName);
//				// 商品コードをキーとした売上金額(初期値として0)を商品別合計売上額HashMapに追加
//				salesCommodity.put(commodityCode, 0L);
//			}
//		} catch (FileNotFoundException e) {
//			System.out.println("商品定義ファイルが存在しません");
//			return;
//		} catch (IOException e) {
//			System.out.println("ファイル入出力の際にエラーが発生しました。");
//			return;
//		} finally {
//			try {
//				br.close();
//			} catch (IOException e) {
//				System.out.println("ファイル入出力の際にエラーが発生しました。");
//				return;
//			}
//		}
//		// コマンドライン引数でディレクトリパスを受け取る
//		File fileArgs = new File(args[0]);
//		// 指定ディレクトリにあるフォルダ、ファイル名をリスト化する
//		// File[] listFile = fileArgs.listFiles();
//		String[] list = fileArgs.list();
//		// リストの要素数分、処理を繰り返す
//		ArrayList<String> salesFileNameList = new ArrayList<String>();
//		ArrayList<Integer> serialNumberList = new ArrayList<Integer>();
//		for (int i = 0; list.length > i; i++) {
//			// 抽出したファイル名のファイル名と拡張子をわけ、ファイル名と拡張子をそれぞれ保持する
//			String listFileName = list[i];
//			int index = listFileName.lastIndexOf(".");
//			String fileName = listFileName.substring(0, index);
//			String extension = listFileName.substring(index + 1);
//			File fileCheck = new File(args[0], listFileName);
//			// 拡張子が[rcd]且つファイル名が8桁の数字かどうか
//			if (fileCheck.isFile() && fileName.matches("\\d{8}")
//					&& "rcd".equals(extension) && listFileName.length() == 12) {
//				// 売上ファイル用コレクションに追加
//				salesFileNameList.add(listFileName);
//				Integer fileNameNumber = Integer.parseInt(fileName);
//				// 連番チェック用コレクション
//				serialNumberList.add(fileNameNumber);
//			}
//		}
//		// 売上ファイル用コレクションの要素数が2つ以上あるか
//		if (2 <= salesFileNameList.size()) {
//			// salesFileNameListを昇順にする
//			Collections.sort(salesFileNameList);
//			// 売上ファイル用コレクション(連番チェック用のほうがいい？)の要素数分処理を繰り返す
//			for (int j = 1; salesFileNameList.size() > j; j++) {
//				if (serialNumberList.get(j) - serialNumberList.get(j - 1) != 1) {
//					System.out.println("売上ファイルが連番になっていません");
//					return;
//				}
//			}
//			// 売上ファイル用コレクションの要素数分処理を繰り返す
//			ArrayList<String> salesReadLineList = new ArrayList<String>();
//			for (int i = 0; salesFileNameList.size() > i; i++) {
//				salesReadLineList = new ArrayList<String>();
//				// コマンドライン引数に売上ファイル用コレクションのファイル名を文字列連結し、ファイルを開く
//				try {
//					br = new BufferedReader(new FileReader(new File(args[0],
//							salesFileNameList.get(i))));
//					// 売上ファイルを最終行まで一行ずつ読み込む
//					while ((record = br.readLine()) != null) {
//						// 読込んだデータを作業用コレクションに追加する
//						salesReadLineList.add(record);
//					}
//					// 作業用コレクションの要素数が3かどうか
//					if (salesReadLineList.size() != 3) {
//						System.out.println("<" + salesFileNameList.get(i)
//								+ ">のフォーマットが不正です");
//						return;
//					}
//					// 支店定義用HashMapのキーにが存在するか
//					if (!branchList.containsKey(salesReadLineList.get(0))) {
//						System.out.println("<" + salesFileNameList.get(i)
//								+ ">の支店コードが不正です");
//						return;
//					}
//					// 商品定義用HashMapのキーにが存在するか
//					if (!commodityList.containsKey(salesReadLineList.get(1))) {
//						System.out.println("<" + salesFileNameList.get(i)
//								+ ">の商品コードが不正です");
//						return;
//					}
//				} catch (IOException e) {
//					System.out.println("ファイル入出力の際にエラーが発生しました。");
//				} finally {
//					try {
//						br.close();
//					} catch (IOException e) {
//						System.out.println("ファイル入出力の際にエラーが発生しました。");
//					}
//				}
//				long beforeSale = 0L;
//				String branchCode = salesReadLineList.get(0);
//				String commodityCode = salesReadLineList.get(1);
//				// 売上額を数値型に変換し保持する
//				beforeSale = Long.parseLong(salesReadLineList.get(2));
//				// 支店別合計売上額HashMapの該当する支店に売上額を加算する
//				long afterBranchSale = salesBranch.get(branchCode) + beforeSale;
//				salesBranch.put(branchCode, afterBranchSale);
//				// 商品別合計売上額HashMapの該当する商品に売上額を加算する
//				long afterCommoditySale = salesCommodity.get(commodityCode)
//						+ beforeSale;
//				salesCommodity.put(commodityCode, afterCommoditySale);
//				// 加算した合計金額が10桁超えていないかどうか
//				if (afterBranchSale >= 10000000000L) {
//					System.out.println("合計金額が10桁を超えました");
//					return;
//				}
//				if (afterCommoditySale >= 10000000000L) {
//					System.out.println("合計金額が10桁を超えました");
//					return;
//				}
//			}
//			// 集計結果出力(支店別集計ファイル)集計結果出力
//			// 支店別合計金額用HashMapの合計金額を降順にしたコレクション(支店合計額降順)を生成
//			List<Entry<String, Long>> branch = new ArrayList<Entry<String, Long>>(
//					salesBranch.entrySet());
//			Collections.sort(branch, new Comparator<Entry<String, Long>>() {
//				@Override
//				public int compare(Entry<String, Long> entry1,
//						Entry<String, Long> entry2) {
//					return (entry2.getValue()).compareTo(entry1.getValue());
//				}
//			});
//			// ファイルオブジェクトを生成し、ファイルを生成する
//			BufferedWriter bwBranch = null;
//			try {
//				File fileBranchOut = new File(args[0], "branch.out");
//				fileBranchOut.createNewFile();
//				bwBranch = new BufferedWriter(new OutputStreamWriter(
//						new FileOutputStream(fileBranchOut, false), "UTF-8"));
//				// 支店合計額降順コレクションを抽出していく
//				for (Entry<String, Long> b : branch) {
//					// 支店合計額降順コレクションのキーの支店コードを用いて支店名用HashMapから支店名を取り出す
//					// [支店コード][支店名][合計売上額]のカンマで区切ったものを文字列連結し、末尾に改行したものを書き込む
//					bwBranch.write(b.getKey() + ",");
//					bwBranch.write(branchList.get(b.getKey()) + ",");
//					bwBranch.write(b.getValue().toString());
//					bwBranch.newLine();
//				}
//			} catch (IOException e) {
//				System.out.println("支店別集計結果ファイルが書き込み時にエラーが発生しました");
//				return;
//			} finally {
//				try {
//					bwBranch.close();
//				} catch (IOException e) {
//					System.out.println("ファイル入出力の際にエラーが発生しました。");
//					return;
//				}
//			}
//			// 集計結果出力(商品別集計ファイル)集計結果出力
//			// 商品別合計金額用HashMapの合計金額を降順にしたコレクション(商品合計額降順)を生成
//			List<Entry<String, Long>> commodity = new ArrayList<Entry<String, Long>>(
//					salesCommodity.entrySet());
//			Collections.sort(commodity, new Comparator<Entry<String, Long>>() {
//				@Override
//				public int compare(Entry<String, Long> entry1,
//						Entry<String, Long> entry2) {
//					return (entry2.getValue()).compareTo(entry1.getValue());
//				}
//			});
//			BufferedWriter bwCommodity = null;
//			try {
//				// ファイルオブジェクトを生成し、ファイルを生成する
//				// コマンドライン引数に文字列連結で「branch.out」を加え、ファイルオブジェクトを生成し、ファイルを生成する
//				File fileCommodityOut = new File(args[0], "commodity.out");
//				fileCommodityOut.createNewFile();
//				bwCommodity = new BufferedWriter(new OutputStreamWriter(
//						new FileOutputStream(fileCommodityOut, false), "UTF-8"));
//				// コレクション(商品合計額降順)抽出していく
//				for (Entry<String, Long> c : commodity) {
//					// 商品合計額降順コレクションのキーの商品コードを用いて商品名用HashMapから商品名を取り出す
//					// [商品コード][商品名][合計売上額]のカンマで区切ったものを文字列連結し、末尾に改行したものを書き込む
//					bwCommodity.write(c.getKey() + ",");
//					bwCommodity.write(commodityList.get(c.getKey()) + ",");
//					bwCommodity.write(c.getValue().toString());
//					bwCommodity.newLine();
//				}
//			} catch (IOException e) {
//				System.out.println("商品別集計結果ファイルが書き込み時にエラーが発生しました");
//				return;
//			} finally {
//				try {
//					bwCommodity.close();
//				} catch (IOException e) {
//					System.out.println("ファイル入出力の際にエラーが発生しました。");
//					return;
//				}
//			}
//		}
//	}
//
// }
