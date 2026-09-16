package idv.const_x.console.io;

public enum ConsoleColorEnum {
		BLACK("BLACK",false),
		BLUE("BLUE",false),
		CYAN("CYAN",false),
		DEFAULT("DEFAULT",false),
		GREEN("GREEN",false),
		MAGENTA("MAGENTA",false),
		RED("RED",false),
		WHITE("WHITE",false),
		YELLOW("YELLOW",false),
		
		BRIGHT_BLACK("BLACK",true),
		BRIGHT_BLUE("BLUE",true),
		BRIGHT_CYAN("CYAN",true),
		BRIGHT_DEFAULT("DEFAULT",true),
		BRIGHT_GREEN("GREEN",true),
		BRIGHT_MAGENTA("MAGENTA",true),
		BRIGHT_RED("RED",true),
		BRIGHT_WHITE("WHITE",true),
		BRIGHT_YELLOW("YELLOW",true);
		
		private boolean bright = false;
		private final String name;

		public boolean getBright() {
			return bright;
		}

		public String getName() {
			return name;
		}

		ConsoleColorEnum(String name, boolean bright) {
			this.bright = bright;
			this.name = name;
		}
	}