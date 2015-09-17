package loader;

import java.io.InputStream;

final public class ResourceLoader {
	
	public static InputStream imageLoader(String path){
		InputStream input = ResourceLoader.class.getResourceAsStream(path);
		if(input==null)
			input = ResourceLoader.class.getResourceAsStream("/"+path);
		return input;
	}
	
}
