package ghost.library.utility;

public class Range {
	public int begin;
	public int end;

	public Range() {

	}

	public Range(int b, int e) 
	{
		// TODO Auto-generated constructor stub
		begin = b;
		end = e;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (!(obj instanceof Range)) 
		{
			return false;
		}
		Range s = (Range) obj;
		return begin == s.begin && end == s.end;
	}

	@Override
	public int hashCode() 
	{
		return begin * 32715 + end;
	}
}
