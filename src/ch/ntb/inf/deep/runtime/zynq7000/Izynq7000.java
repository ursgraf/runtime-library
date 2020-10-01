package ch.ntb.inf.deep.runtime.zynq7000;

import ch.ntb.inf.deep.runtime.arm32.Iarm32;

// Auto generated file (2020-10-01 13:39:16)

public interface Izynq7000 extends Iarm32 {

	// System constants of CPU zynq7000
	public static final int interruptCtlrDistr = 0xf8f01000;
	public static final int privateTimer = 0xf8f00600;
	public static final int globalTimer = 0xf8f00200;
	public static final int interruptCtlr = 0xf8f00100;
	public static final int cpuPrivateReg = 0xf8900000;
	public static final int slcrReg = 0xf8000000;
	public static final int gpioController = 0xe000a000;
	public static final int uartController = 0xe0000000;
	public static final int ioReg = 0xe0000000;
	public static final int heapSize = 0x10000;
	public static final int stackSizeIRQ = 0x1000;
	public static final int stackSizeSVC = 0x1000;
	public static final int sysTabBaseAddr = 0x1000;
	public static final int excpCodeSize = 0x1000;
	public static final int excpCodeBase = 0x0;
	public static final int OCM_Size = 0x40000;
	public static final int OCM_BaseAddr = 0x0;

	// Specific registers of CPU zynq7000
	public static final int GPIO_MASK_LSW0 = 0xe000a000;
	public static final int GPIO_MASK_MSW0 = 0xe000a004;
	public static final int GPIO_MASK_LSW1 = 0xe000a008;
	public static final int GPIO_MASK_MSW1 = 0xe000a00c;
	public static final int GPIO_OUT0 = 0xe000a040;
	public static final int GPIO_OUT1 = 0xe000a044;
	public static final int GPIO_IN0 = 0xe000a060;
	public static final int GPIO_IN1 = 0xe000a064;
	public static final int GPIO_DIR0 = 0xe000a204;
	public static final int GPIO_OUT_EN0 = 0xe000a208;
	public static final int GPIO_DIR1 = 0xe000a244;
	public static final int GPIO_OUT_EN1 = 0xe000a248;
	public static final int GTCR_L = 0xf8f00200;
	public static final int GTCR_U = 0xf8f00204;
	public static final int GTCR = 0xf8f00208;
	public static final int PTLR = 0xf8f00600;
	public static final int PTCOUNT = 0xf8f00604;
	public static final int PTCR = 0xf8f00608;
	public static final int PTISR = 0xf8f0060c;
	public static final int SLCR_LOCK = 0xf8000004;
	public static final int SLCR_UNLOCK = 0xf8000008;
	public static final int SLCR_LOCKSTA = 0xf800000c;
	public static final int ARM_PLL_CTRL = 0xf8000100;
	public static final int DDR_PLL_CTRL = 0xf8000104;
	public static final int IO_PLL_CTRL = 0xf8000108;
	public static final int PLL_STATUS = 0xf800010c;
	public static final int ARM_PLL_CFG = 0xf8000110;
	public static final int DDR_PLL_CFG = 0xf8000114;
	public static final int IO_PLL_CFG = 0xf8000118;
	public static final int ARM_CLK_CTRL = 0xf8000120;
	public static final int DDR_CLK_CTRL = 0xf8000124;
	public static final int DCI_CLK_CTRL = 0xf8000128;
	public static final int APER_CLK_CTRL = 0xf800012c;
	public static final int LQSPI_CLK_CTRL = 0xf800014c;
	public static final int UART_CLK_CTRL = 0xf8000154;
	public static final int SPI_CLK_CTRL = 0xf8000158;
	public static final int DBG_CLK_CTRL = 0xf8000164;
	public static final int PCAP_CLK_CTRL = 0xf8000168;
	public static final int TOPSW_CLK_CTRL = 0xf8000168;
	public static final int FPGA0_CLK_CTRL = 0xf8000170;
	public static final int FPGA1_CLK_CTRL = 0xf8000180;
	public static final int FPGA2_CLK_CTRL = 0xf8000190;
	public static final int FPGA3_CLK_CTRL = 0xf80001a0;
	public static final int CLK_621_TRUE = 0xf80001c4;
	public static final int PSS_RST_CTRL = 0xf8000200;
	public static final int DDR_RST_CTRL = 0xf8000204;
	public static final int FPGA_RST_CTRL = 0xf8000240;
	public static final int REBOOT_STATUS = 0xf8000258;
	public static final int BOOT_MODE = 0xf800025c;
	public static final int MIO_PIN_00 = 0xf8000700;
	public static final int MIO_PIN_01 = 0xf8000704;
	public static final int MIO_PIN_07 = 0xf800071c;
	public static final int MIO_PIN_09 = 0xf8000724;
	public static final int MIO_PIN_10 = 0xf8000728;
	public static final int MIO_PIN_11 = 0xf800072c;
	public static final int MIO_PIN_12 = 0xf8000730;
	public static final int MIO_PIN_13 = 0xf8000734;
	public static final int MIO_PIN_14 = 0xf8000738;
	public static final int MIO_PIN_15 = 0xf800073c;
	public static final int MIO_PIN_47 = 0xf80007bc;
	public static final int MIO_PIN_48 = 0xf80007c0;
	public static final int MIO_PIN_49 = 0xf80007c4;
	public static final int MIO_PIN_51 = 0xf80007cc;
	public static final int LVL_SHFTR_EN = 0xf8000900;
	public static final int OCM_CFG = 0xf8000910;
	public static final int GPIOB_CTRL = 0xf8000b00;
	public static final int DDRIOB_ADDR0 = 0xf8000b40;
	public static final int DDRIOB_ADDR1 = 0xf8000b44;
	public static final int DDRIOB_DATA0 = 0xf8000b48;
	public static final int DDRIOB_DATA1 = 0xf8000b4c;
	public static final int DDRIOB_DIFF0 = 0xf8000b50;
	public static final int DDRIOB_DIFF1 = 0xf8000b54;
	public static final int DDRIOB_CLOCK = 0xf8000b58;
	public static final int DDRIOB_DRIVE_SLEW_ADDR = 0xf8000b5c;
	public static final int DDRIOB_DRIVE_SLEW_DATA = 0xf8000b60;
	public static final int DDRIOB_DRIVE_SLEW_DIFF = 0xf8000b64;
	public static final int DDRIOB_DRIVE_SLEW_CLOCK = 0xf8000b68;
	public static final int DDRIOB_DDR_CTRL = 0xf8000b6c;
	public static final int DDRIOB_DCI_CTRL = 0xf8000b70;
	public static final int DDRIOB_DCI_STATUS = 0xf8000b74;
	public static final int ICCICR = 0xf8f00100;
	public static final int ICCPMR = 0xf8f00104;
	public static final int ICCBPR = 0xf8f00108;
	public static final int ICCIAR = 0xf8f0010c;
	public static final int ICCEOIR = 0xf8f00110;
	public static final int ICCIDR = 0xf8f001fc;
	public static final int ICDDCR = 0xf8f01000;
	public static final int ICDICTR = 0xf8f01004;
	public static final int ICDIIDR = 0xf8f01008;
	public static final int ICDISER0 = 0xf8f01100;
	public static final int ICDISER1 = 0xf8f01104;
	public static final int ICDISER2 = 0xf8f01108;
	public static final int ICDICER0 = 0xf8f01180;
	public static final int ICDICER1 = 0xf8f01184;
	public static final int ICDICER2 = 0xf8f01188;
	public static final int ICDIPR0 = 0xf8f01400;
	public static final int ICDIPR1 = 0xf8f01404;
	public static final int ICDIPR20 = 0xf8f01450;
	public static final int ICDIPTR0 = 0xf8f01800;
	public static final int ICDIPTR1 = 0xf8f01804;
	public static final int ICDIPTR2 = 0xf8f01808;
	public static final int ICDIPTR20 = 0xf8f01850;
	public static final int ICDICFR0 = 0xf8f01c00;
	public static final int ICDICFR1 = 0xf8f01c04;
	public static final int ICDICFR2 = 0xf8f01c08;
	public static final int ICDICFR3 = 0xf8f01c0c;
	public static final int ICDICFR4 = 0xf8f01c10;
	public static final int ICDICFR5 = 0xf8f01c14;
	public static final int UART0_CR = 0xe0000000;
	public static final int UART0_MR = 0xe0000004;
	public static final int UART0_IER = 0xe0000008;
	public static final int UART0_IDR = 0xe000000c;
	public static final int UART0_IMR = 0xe0000010;
	public static final int UART0_ISR = 0xe0000014;
	public static final int UART0_BAUDGEN = 0xe0000018;
	public static final int UART0_RXTOUT = 0xe000001c;
	public static final int UART0_RX_FIFO_LEVEL = 0xe0000020;
	public static final int UART0_MODEMCR = 0xe0000024;
	public static final int UART0_MODEMSR = 0xe0000028;
	public static final int UART0_SR = 0xe000002c;
	public static final int UART0_FIFO = 0xe0000030;
	public static final int UART0_BAUDDIV = 0xe0000034;
	public static final int UART0_FLOWDELAY = 0xe0000038;
	public static final int UART0_TX_FIFO_LEVEL = 0xe0000044;
	public static final int UART1_CR = 0xe0001000;
	public static final int UART1_MR = 0xe0001004;
	public static final int UART1_IER = 0xe0001008;
	public static final int UART1_IDR = 0xe000100c;
	public static final int UART1_IMR = 0xe0001010;
	public static final int UART1_ISR = 0xe0001014;
	public static final int UART1_BAUDGEN = 0xe0001018;
	public static final int UART1_RXTOUT = 0xe000101c;
	public static final int UART1_RX_FIFO_LEVEL = 0xe0001020;
	public static final int UART1_MODEMCR = 0xe0001024;
	public static final int UART1_MODEMSR = 0xe0001028;
	public static final int UART1_SR = 0xe000102c;
	public static final int UART1_FIFO = 0xe0001030;
	public static final int UART1_BAUDDIV = 0xe0001034;
	public static final int UART1_FLOWDELAY = 0xe0001038;
	public static final int UART1_TX_FIFO_LEVEL = 0xe0001044;
}