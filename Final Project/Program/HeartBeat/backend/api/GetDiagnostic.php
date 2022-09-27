<?php
 include_once "koneksi.php";

	class usr{}
	
	$user_id = $_GET['user_id'];
	
	$query = mysqli_query($con, "SELECT * FROM `diagnostic` WHERE user_id = '".$user_id."'");
			
	if ($query){
		$response = new usr();
		$response->status = 1;
		$response->message = "Ambil Diagnostic Berhasil";
		$datas = array();

		while($row = mysqli_fetch_assoc($query)){
			$datas = array(array(
						"user_id" => $row['user_id'],
						"perokok" => $row['perokok'],
						"diabetes" => $row['diabetes'],
						"kolestrol" => $row['kolestrol'],
						"hipertensi" => $row['hipertensi']
			));		
		}
		
		$response->data = $datas;

		die(json_encode($response));
		
	} else { 
		$response = new usr();
		$response->status = 0;
		$response->message = "gagal mendapatkan Signal";
		$response->data = null;
		die(json_encode($response));
	}
	
	mysqli_close($con);
?>