import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestMain {

	public static void main(String args[]) {
			
		String sql ="select o.*,m.uname from es_order o left join es_member t  on o.member_id=t.member_id where o.disabled=0  and ( ( o.payment_type!='cod' and o.payment_id!=8  and  o.status=2)  or ( o.payment_type='cod' and  o.status=0))  ORDER BY o.order_id desc";
    
		System.out.println(removeSelect(sql));
	}
	
	private static String removeSelect(String sql){
		sql=sql.toLowerCase();
		Pattern p = Pattern.compile("\\(.*\\)",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			int c = m.end()-m.start();
			m.appendReplacement(sb, getStr(c,"~"));
		}
		m.appendTail(sb);
		
		String replacedSql = sb.toString();
		
		return sql.substring(replacedSql.indexOf("from"));
	}
	
	private static String getStr(int num, String str) {
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < num; i++) {
			sb.append(str);
		}
		return sb.toString();
	}
}
