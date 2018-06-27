/**
 * Restart contains method to restart application server
 */
package org.frame.common.context;

import org.frame.common.io.File;
import org.frame.common.path.Path;

public class Restart {

	/**
	 * restart application server
	 */
	public void restart() {
		Path path = new Path();
		String bin = path.server() + "/bin";
		File dest = new File(bin + "/tomcat_restart.bat");
		if (!dest.exists()) {
			String src = path.resource("org/frame/common/resource/tomcat_restart.bat");
			new File(src).copy(dest);
		}
		
		new OperateSystem().bat(dest.getAbsolutePath().replace("\\", "/"));
	}
	
}
