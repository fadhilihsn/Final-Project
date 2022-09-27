<?php
 include_once "koneksi.php";

	class usr{}
	
	$signal_id = $_GET['signal_id'];
	
	$query = mysqli_query($con, "SELECT * FROM `signal` WHERE id = '".$signal_id."'");
			
	if ($query){
		$response = new usr();
		$response->status = 1;
		$response->message = "Ambil Signal Berhasil";
		$datas = new usr();

		while($row = mysqli_fetch_assoc($query)){
			$datas = array(
						"id" => $row['id'],
						"pulse" => $row['pulse'],
						"cycles" => $row['cycles'],
						"duration" => $row['duration'],
						"signals" => $row['signals'],
						"user_id" => $row['user_id'],
						"timestamp" => $row['timestamp']
			);		
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