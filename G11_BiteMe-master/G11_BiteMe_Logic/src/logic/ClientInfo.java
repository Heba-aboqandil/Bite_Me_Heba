package logic;

public class ClientInfo {
	private String name;
	private String address;
	private String Status;

	public ClientInfo(String name, String address, String status) {
		this.name = name;
		this.address = address;
		Status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}
}
