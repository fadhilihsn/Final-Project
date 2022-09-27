<?php
 include_once "koneksi.php";

	class usr{}
	
	$query = "";
	if(isset($_GET['user_id'])){
		$user_id = $_GET['user_id'];
		$query = mysqli_query($con, "SELECT * FROM `signal` WHERE user_id='$user_id'");
	}else {
		$query = mysqli_query($con, "SELECT * FROM `signal`");
	}
			
	if ($query){
		$response = new usr();
		$response->status = 1;
		$response->message = "Ambil Signal Berhasil";
		$datas = array();

		while($row = mysqli_fetch_assoc($query)){
			array_push($datas,array(
						"id" => $row['id'],
						"pulse" => $row['pulse'],
						"cycles" => $row['cycles'],
						"duration" => $row['duration'],
						"signals" => $row['signals'],
						"user_id" => $row['user_id'],
						"timestamp" => $row['timestamp']
			));		
		}
		
		$response->data = $datas;

		die(json_encode($response));
		
	} else { 
		$response = new usr();
		$response->status = 0;
		$response->message = "Gagal mengambil Signal";
		$response->data = null;
		die(json_encode($response));
	}
	
	mysqli_close($con);
?>