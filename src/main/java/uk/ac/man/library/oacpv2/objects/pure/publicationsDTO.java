package uk.ac.man.library.oacpv2.objects.pure;

import java.util.List;

public class publicationsDTO {
	
	
	private int draw;
	private Long recordsTotal;
	private Long recordsFiltered;
	
	private List<PublicationsColumns> data;
	
	public List<PublicationsColumns> getData() {
		return data;
	}

	public void setData(List<PublicationsColumns> data) {
		this.data = data;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public Long getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(Long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public Long getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(Long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	
	
}
