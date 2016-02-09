package com.wattaina.sales_aggregate_system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class SalesAggregate {
	public static void main(String[] args) {
		// コマンドライン引数の中身があるかどうか
		if (args.length == 0) {
			System.out.println("集計システムに必要なファイルがあるディレクトリのパスを、コマンドプロンプトから入力してください");
			return;
		}
		HashMap<String, String> branchList = new HashMap<String, String>();
		HashMap<String, Long> salesBranch = new HashMap<String, Long>();
		String branchFormatCheck = "^\\d{3}";
		// ファイルの読込メソッドの呼び出し(支店)
		String result = readFile(args[0], "branch.lst", branchList, salesBranch, branchFormatCheck);
		if (result != null) {
			System.out.println("支店" + result);
			return;
		}
		HashMap<String, String> commodityList = new HashMap<String, String>();
		HashMap<String, Long> salesCommodity = new HashMap<String, Long>();
		String commodityFormatCheck = "[a-zA-Z0-9]{8}";
		// ファイルの読込メソッドの呼び出し(商品)
		result = readFile(args[0], "commodity.lst", commodityList, salesCommodity, commodityFormatCheck);
		if (result != null) {
			System.out.println("商品" + result);
			return;
		}
		// コマンドライン引数でディレクトリパスを受け取る
		File fileArgs = new File(args[0]);
		// 指定ディレクトリにあるフォルダ、ファイル名をリスト化する
		String[] list = fileArgs.list();
		// リストの要素数分、処理を繰り返す
		ArrayList<String> salesFileNameList = new ArrayList<String>();
		ArrayList<Integer> serialNumberList = new ArrayList<Integer>();
		// 抽出したファイル名のファイル名と拡張子をわけ、ファイル名と拡張子をそれぞれ保持する
		for (int i = 0; list.length > i; i++) {
			String listFileName = list[i];
			int index = listFileName.lastIndexOf(".");
			String fileName = listFileName.substring(0, index);
			String extension = listFileName.substring(index + 1);
			File fileCheck = new File(args[0], listFileName);
			// 拡張子が[rcd]且つファイル名が8桁の数字かどうか
			if (fileCheck.isFile() && fileName.matches("\\d{8}") && "rcd".equals(extension) && listFileName.length() == 12) {
				// 売上ファイル用コレクションに追加
				salesFileNameList.add(listFileName);
				Integer fileNameNumber = Integer.parseInt(fileName);
				// 連番チェック用コレクション
				serialNumberList.add(fileNameNumber);
			}
		}
		// 売上ファイル用コレクションの要素数が2つ以上あるか
		if (2 <= salesFileNameList.size()) {
			// salesFileNameListを昇順にする
			Collections.sort(salesFileNameList);
			// 売上ファイル用コレクション(連番チェック用のほうがいい？)の要素数分処理を繰り返す
			for (int j = 1; salesFileNameList.size() > j; j++) {
				if (serialNumberList.get(j) - serialNumberList.get(j - 1) != 1) {
					System.out.println("売上ファイルが連番になっていません");
					return;
				}
			}
		}
		// 売上ファイル用コレクションの要素数分処理を繰り返す
		ArrayList<String> salesReadLineList = new ArrayList<String>();
		for (int i = 0; salesFileNameList.size() > i; i++) {
			salesReadLineList = new ArrayList<String>();
			// コマンドライン引数に売上ファイル用コレクションのファイル名を文字列連結し、ファイルを開く
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(new File(args[0], salesFileNameList.get(i))));
				// 売上ファイルを最終行まで一行ずつ読み込む
				String record = null;
				while ((record = br.readLine()) != null) {
					// 読込んだデータを作業用コレクションに追加する
					salesReadLineList.add(record);
				}
				// 作業用コレクションの要素数が3かどうか
				if (salesReadLineList.size() != 3) {
					System.out.println("<" + salesFileNameList.get(i) + ">のフォーマットが不正です");
					return;
				}
				// 支店定義用HashMapのキーにが存在するか
				if (!branchList.containsKey(salesReadLineList.get(0))) {
					System.out.println("<" + salesFileNameList.get(i) + ">の支店コードが不正です");
					return;
				}
				// 商品定義用HashMapのキーにが存在するか
				if (!commodityList.containsKey(salesReadLineList.get(1))) {
					System.out.println("<" + salesFileNameList.get(i) + ">の商品コードが不正です");
					return;
				}
			} catch (IOException e) {
				System.out.println("ファイル入出力の際にエラーが発生しました。");
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("ファイル入出力の際にエラーが発生しました。");
				}
			}
			long beforeSale = 0L;
			String branchCode = salesReadLineList.get(0);
			String commodityCode = salesReadLineList.get(1);
			// 売上額を数値型に変換し保持する
			beforeSale = Long.parseLong(salesReadLineList.get(2));
			// 支店別合計売上額HashMapの該当する支店に売上額を加算する
			long afterBranchSale = salesBranch.get(branchCode) + beforeSale;
			salesBranch.put(branchCode, afterBranchSale);
			// 商品別合計売上額HashMapの該当する商品に売上額を加算する
			long afterCommoditySale = salesCommodity.get(commodityCode) + beforeSale;
			salesCommodity.put(commodityCode, afterCommoditySale);
			// 加算した合計金額が10桁超えていないかどうか
			if (afterBranchSale >= 10000000000L) {
				System.out.println("合計金額が10桁を超えました");
				return;
			}
			if (afterCommoditySale >= 10000000000L) {
				System.out.println("合計金額が10桁を超えました");
				return;
			}
		}
		// 集計結果出力
		// 集計結果出力メソッドの呼び出し(支店)
		if (!aggregateResultOutput(salesBranch, args[0], "branch.out", branchList)) {
			System.out.println("支店別集計結果ファイルが書き込み時にエラーが発生しました");
			return;
		}
		// 集計結果出力メソッドの呼び出し(商品)
		if (!aggregateResultOutput(salesCommodity, args[0], "commodity.out", commodityList)) {
			System.out.println("商品別集計結果ファイルが書き込み時にエラーが発生しました");
			return;
		}
	}

	// ファイル読込みメソッド
	public static String readFile(String directoryPath, String purpose,
			HashMap<String, String> useList, HashMap<String, Long> salesMap, String formatCheck) {
		BufferedReader br = null;
		File existFile = new File(directoryPath, purpose);
		if (!existFile.exists()) {
			return "定義ファイルが存在しません";
		}
		if (!existFile.isFile()) {
			return "定義ファイルが存在しません";
		}
		try {
			br = new BufferedReader(new FileReader(new File(directoryPath, purpose)));
			String record;
			// ファイルを一行ずつ読込む
			while ((record = br.readLine()) != null) {
				// 読み込んだデータをカンマで分割
				String[] splittedReadLine = record.split(",", -1);
				if (splittedReadLine.length != 2) {
					return "定義ファイルのフォーマットが不正です";
				}
				// 分割したデータを、それぞれ「コード」「名前」のデータを保持する
				String useCode = splittedReadLine[0];
				String useName = splittedReadLine[1];
				// コードが指定されたフォーマットでなければエラー処理
				if (!useCode.matches(formatCheck)) {
					return "定義ファイルのフォーマットが不正です";
				}
				// コードをキーとした名前を定義用HashMapに追加
				useList.put(useCode, useName);
				// コードをキーとした売上金額(初期値として0)を合計売上額HashMapに追加
				salesMap.put(useCode, 0L);
			}
		} catch (NullPointerException e) {
			return "ファイル入出力の際にエラーが発生しました。";
		} catch (Exception e) {
			return "ファイル入出力の際にエラーが発生しました。";
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				return "ファイル入出力の際にエラーが発生しました。";
			}
		}
		return null;
	}

	// 集計結果出力メソッド
	public static boolean aggregateResultOutput(HashMap<String, Long> salesMap,
			String directoryPath, String makeFileName,HashMap<String, String> useListMap) {
		// 支店(商品)別合計金額用HashMapの合計金額を降順にしたコレクション(合計額降順)を生成
		List<Entry<String, Long>> descendingOrder = new ArrayList<Entry<String, Long>>(salesMap.entrySet());
		Collections.sort(descendingOrder,new Comparator<Entry<String, Long>>() {
					@Override
					public int compare(Entry<String, Long> entry1,Entry<String, Long> entry2) {
						return (entry2.getValue()).compareTo(entry1.getValue());
					}
				});
		// ファイルオブジェクトを生成し、ファイルを生成する
		BufferedWriter bw = null;
		try {
			File fileOut = new File(directoryPath, makeFileName);
			fileOut.createNewFile();
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOut, false), "UTF-8"));
			// 合計額降順コレクションを抽出していく
			for (Entry<String, Long> descendingOrderMap : descendingOrder) {
				// 合計額降順コレクションのキーの支店コードを用いて支店名用HashMapから支店名を取り出す
				// [コード][名前][合計売上額]のカンマで区切ったものを文字列連結し、末尾に改行したものを書き込む
				bw.write(descendingOrderMap.getKey() + ",");
				bw.write(useListMap.get(descendingOrderMap.getKey()) + ",");
				bw.write(descendingOrderMap.getValue().toString());
				bw.newLine();
			}
		} catch (IOException e) {
			return false;
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				System.out.println("ファイル入出力の際にエラーが発生しました。");
				return false;
			}
		}
		return true;
	}
}
// メソッドわけ 集計結果出力→ファイル読込み