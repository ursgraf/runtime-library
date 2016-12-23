package ch.ntb.sysp.spezausb.tof;

public interface VL6180XAddr {
	public static final int IDENTIFICATION__MODEL_ID 				= 0x000;
	public static final int IDENTIFICATION__MODEL_REV_MAJOR 		= 0x001;
	public static final int IDENTIFICATION__MODEL_REV_MINOR 		= 0x002;
	public static final int IDENTIFICATION__MODULE_REV_MAJOR 		= 0x003;
	public static final int IDENTIFICATION__MODULE_REV_MINOR 		= 0x004;
	public static final int IDENTIFICATION__DATE_HI 				= 0x006;
	public static final int IDENTIFICATION__DATE_LO 				= 0x007;
	public static final int IDENTIFICATION__TIME 					= 0x008;
	
	public static final int SYSTEM__MODE_GPIO0 						= 0x010;
	public static final int SYSTEM__MODE_GPIO1 						= 0x011;
	public static final int SYSTEM__HISTORY_CTRL 					= 0x012;
	public static final int SYSTEM__INTERRUPT_CONFIG_GPIO 			= 0x014;
	public static final int SYSTEM__INTERRUPT_CLEAR 				= 0x015;
	public static final int SYSTEM__FRESH_OUT_OF_RESET 				= 0x016;
	public static final int SYSTEM__GROUPED_PARAMETER_HOLD 			= 0x017;
	
	public static final int SYSRANGE__START 						= 0x018;
	public static final int SYSRANGE__THRESH_HIGH 					= 0x019;
	public static final int SYSRANGE__THRESH_LOW 					= 0x01A;
	public static final int SYSRANGE__INTERMEASUREMENT_PERIOD 		= 0x01B;
	public static final int SYSRANGE__MAX_CONVERGENCE_TIME 			= 0x01C;
	public static final int SYSRANGE__CROSSTALK_COMPENSATION_RATE 	= 0x01E;
	public static final int SYSRANGE__CROSSTALK_VALID_HEIGHT 		= 0x021;
	public static final int SYSRANGE__EARLY_CONVERGENCE_ESTIMATE = 0x022;
	public static final int SYSRANGE__PART_TO_PART_RANGE_OFFSET = 0x024;
	public static final int SYSRANGE__RANGE_IGNORE_VALID_HEIGHT = 0x025;
	public static final int SYSRANGE__RANGE_IGNORE_THRESHOLD = 0x026;
	public static final int SYSRANGE__MAX_AMBIENT_LEVEL_MULT = 0x02C;
	public static final int SYSRANGE__RANGE_CHECK_ENABLES = 0x02D;
	public static final int SYSRANGE__VHV_RECALIBRATE = 0x02E;
	public static final int SYSRANGE__VHV_REPEAT_RATE = 0x031;
	
	public static final int SYSALS__START = 0x038;
	public static final int SYSALS__THRESH_HIGH = 0x03A;
	public static final int SYSALS__THRESH_LOW = 0x03C;
	public static final int SYSALS__INTERMEASUREMENT_PERIOD = 0x03E;
	public static final int SYSALS__ANALOGUE_GAIN = 0x03F;
	public static final int SYSALS__INTEGRATION_PERIOD = 0x040;
	
	public static final int RESULT__RANGE_STATUS = 0x04D;
	public static final int RESULT__ALS_STATUS = 0x04E;
	public static final int RESULT__INTERRUPT_STATUS_GPIO = 0x04F;
	public static final int RESULT__ALS_VAL = 0x050;
	public static final int RESULT__HISTORY_BUFFER_x = 0x052;					// 0x052 + x * 0x2 ( x = 0 to 7 )
	public static final int RESULT__RANGE_VAL = 0x062;
	public static final int RESULT__RANGE_RAW = 0x064;
	public static final int RESULT__RANGE_RETURN_RATE = 0x066;					// 2 byte
	public static final int RESULT__RANGE_REFERENCE_RATE = 0x068;				// 2 byte
	public static final int RESULT__RANGE_RETURN_SIGNAL_COUNT = 0x06C;			// 4 byte
	public static final int RESULT__RANGE_REFERENCE_SIGNAL_COUNT = 0x070;		// 4 byte
	public static final int RESULT__RANGE_RETURN_AMB_COUNT = 0x074;				// 4 byte
	public static final int RESULT__RANGE_REFERENCE_AMB_COUNT = 0x078;			// 4 byte
	public static final int RESULT__RANGE_RETURN_CONV_TIME = 0x07C;				// 4 byte
	public static final int RESULT__RANGE_REFERENCE_CONV_TIME = 0x080;			// 4 byte
	
	public static final int READOUT__AVERAGING_SAMPLE_PERIOD = 0x10A;
	public static final int FIRMWARE__BOOTUP = 0x119;
	public static final int FIRMWARE__RESULT_SCALER = 0x120;
	public static final int I2C_SLAVE__DEVICE_ADDRESS = 0x212;
	public static final int INTERLEAVED_MODE__ENABLE = 0x2A3;
	
}
