/** ***************************************************************************
**  _________     _____      _____    ____  _____    ___  ____               **
** |_   ___  |  |_   _|     |_   _|  |_   \|_   _|  |_  ||_  _|              **
**   | |_  \_|    | |         | |      |   \ | |      | |_/ /                **
**   |  _|        | |   _     | |      | |\ \| |      |  __'.                **
**  _| |_        _| |__/ |   _| |_    _| |_\   |_    _| |  \ \_              **
** |_____|      |________|  |_____|  |_____|\____|  |____||____|             **
**                                                                           **
*******************************************************************************
**                                                                           **
** fLink definitions                                                         **
**                                                                           **
**  THIS FILE WAS CREATED AUTOMATICALLY - do not change                      **
**                                                                           **
**  Created with: flinkinterface/func_id/                                    **
**                   create_FLinkDefinitions.java_flinkdeep.sh               **
**                                                                           **
*******************************************************************************
*/

package ch.ntb.inf.deep.flink.core;

public interface FlinkDefinitions {

	public static final int REGISTER_WIDTH		=		4;		// byte
	public static final int REGISTER_WIDTH_BIT	=		REGISTER_WIDTH*8;	
	public static final int HEADER_SIZE		=			16;		// byte
	public static final int SUBHEADER_SIZE	=			16;		// byte
	public static final int TOTAL_HEADER_SIZE = 		HEADER_SIZE + SUBHEADER_SIZE;
	
	
	public static final int TYPE_OFFSET = 				0x0;
	public static final int SIZE_OFFSET = 				0x4;
	public static final int CHANNEL_OFFSET = 			0x8;
	public static final int UNIQUE_ID_OFFSET = 			0xC;
	public static final int MOD_STATUS_OFFSET = 		0x10;
	public static final int MOD_CONF_OFFSET =			0x14;
	
	
	public static final int INFO_DEVICE_ID =			0x00;
	public static final int ANALOG_INPUT_INTERFACE_ID =	0x01;
	public static final int ANALOG_OUTPUT_INTERFACE_ID=	0x02;
	public static final int GPIO_INTERFACE_ID=			0x05;
	public static final int COUNTER_INTERFACE_ID =		0x06;
	public static final int PWM_INTERFACE_ID =			0x0C;
	public static final int PPWA_INTERFACE_ID =			0x0D;
	public static final int WD_INTERFACE_ID =			0x10;
 
 	public static final int INTERFACE_TYPE_MASK = 		0xFFFF;
 	public static final int INFO_DEVICE_SIZE = 			0x80;


 }
