/**
 * Call contains methods to execute command
 */
package org.frame.common.context;

import java.io.IOException;

import org.frame.common.lang.StringHelper;

public class OperateSystem {

	protected enum OS {
		Any("any"),
		Linux("Linux"),
		Mac_OS("Mac OS"),
		Mac_OS_X("Mac OS X"),
		Windows("Windows"),
		OS2("OS/2"),
		Solaris("Solaris"),
		SunOS("SunOS"),
		MPEiX("MPE/iX"),
		HP_UX("HP-UX"),
		AIX("AIX"),
		OS390("OS/390"),
		FreeBSD("FreeBSD"),
		Irix("Irix"),
		Digital_Unix("Digital Unix"),
		NetWare_411("NetWare"),
		OSF1("OSF1"),
		OpenVMS("OpenVMS"),
		Others("Others");

		private OS(String os) {
			this.os = os;
		}

		public String toString() {
			return os;
		}

		private String os;
	}
	
	public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0;
    }
      
    public static boolean isMacOS() {
        return System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0 && System.getProperty("os.name").toLowerCase().indexOf("os") >0 && System.getProperty("os.name").toLowerCase().indexOf("x") < 0; 
    }
      
    public static boolean isMacOSX() {
        return System.getProperty("os.name").toLowerCase().indexOf("mac") >=0 && System.getProperty("os.name").toLowerCase().indexOf("os")>0 && System.getProperty("os.name").toLowerCase().indexOf("x") > 0;
    }
      
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0;
    }
      
    public static boolean isOS2() {
        return System.getProperty("os.name").toLowerCase().indexOf("os/2") >= 0;
    }
      
    public static boolean isSolaris() {
        return System.getProperty("os.name").toLowerCase().indexOf("solaris") >= 0;
    }
      
    public static boolean isSunOS() {
        return System.getProperty("os.name").toLowerCase().indexOf("sunos") >= 0;
    }
      
    public static boolean isMPEiX() {
        return System.getProperty("os.name").toLowerCase().indexOf("mpe/ix") >= 0;
    }
      
    public static boolean isHPUX() {
        return System.getProperty("os.name").toLowerCase().indexOf("hp-ux") >= 0;
    }
      
    public static boolean isAix() {
        return System.getProperty("os.name").toLowerCase().indexOf("aix") >= 0;
    }
      
    public static boolean isOS390() {  
        return System.getProperty("os.name").toLowerCase().indexOf("os/390") >= 0;
    }
      
    public static boolean isFreeBSD() {
        return System.getProperty("os.name").toLowerCase().indexOf("freebsd") >= 0;
    }
      
    public static boolean isIrix() {
        return System.getProperty("os.name").toLowerCase().indexOf("irix") >= 0;
    }
      
    public static boolean isDigitalUnix() {
        return System.getProperty("os.name").toLowerCase().indexOf("digital") >= 0 && System.getProperty("os.name").toLowerCase().indexOf("unix") > 0;
    }
      
    public static boolean isNetWare() {
        return System.getProperty("os.name").toLowerCase().indexOf("netware") >= 0;
    }
      
    public static boolean isOSF1() {
        return System.getProperty("os.name").toLowerCase().indexOf("osf1") >= 0;
    }
      
    public static boolean isOpenVMS() {
        return System.getProperty("os.name").toLowerCase().indexOf("openvms") >= 0;
    }
	
    public static OS getName() {
    	OS result = null;

    	if (isAix()) {
    		result = OS.AIX;
    	} else if (isDigitalUnix()) {
    		result = OS.Digital_Unix;
    	} else if (isFreeBSD()) {
    		result = OS.FreeBSD;
    	} else if (isHPUX()) {
    		result = OS.HP_UX;
    	} else if (isIrix()) {
    		result = OS.Irix;
    	} else if (isLinux()) {
    		result = OS.Linux;
    	} else if (isMacOS()) {
    		result = OS.Mac_OS;
    	} else if (isMacOSX()) {
    		result = OS.Mac_OS_X;
    	} else if (isMPEiX()) {
    		result = OS.MPEiX;
    	} else if (isNetWare()) {
    		result = OS.NetWare_411;
    	} else if (isOpenVMS()) {
    		result = OS.OpenVMS;
    	} else if (isOS2()) {
    		result = OS.OS2;
    	} else if (isOS390()) {
    		result = OS.OS390;
    	} else if (isOSF1()) {
    		result = OS.OSF1;
    	} else if (isSolaris()) {
    		result = OS.Solaris;
    	} else if (isSunOS()) {
    		result = OS.SunOS;
    	} else if (isWindows()) {
    		result = OS.Windows;
    	} else {
    		result = OS.Others;
    	}

        return result;
    }
	
	/**
	 * execute a bat file
	 * 
	 * @param batPath path of bat file
	 */
	public Process bat(String batPath) {
		String cmd = "cmd /k start " + batPath;
		return this.execute(cmd);
	}
	
	/**
	 * execute a command
	 * 
	 * @param cmd string of command
	 */
	public Process execute(String cmd) {
		Process process;
		try {
			process = Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			process = null;
			e.printStackTrace();
		}
		
		return process;
	}
	
	public static void main(String[] args) {
		try {
			Process process = Runtime.getRuntime().exec("ping 10.10.10.10");
			String str = new StringHelper().inputStream2String(process.getInputStream(), "GBK");
			System.out.println(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
