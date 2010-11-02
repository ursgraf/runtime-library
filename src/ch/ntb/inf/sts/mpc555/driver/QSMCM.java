package ch.ntb.inf.sts.mpc555.driver;

import ch.ntb.inf.sts.internal.SYS;
import ch.ntb.inf.sts.mpc555.Kernel;

/*changes:
 * 15.05.07	NTB/SP	creation
 */
/**
 * constant definitions for QSMCM<br>
 */
public class QSMCM{

	// interrupt levels

	/**
	 * SCI Interrupt level
	 */
	public static final int SCI_IntLevel = 5;
	/**
	 * SPI Interrupt level
	 */
	public static final int SPI_IntLevel = 4;

	// registers

	/**
	 * QSMCM configuration
	 */
	public static final int QSMCM_REG = Kernel.UIMB + 0x5000;
	/**
	 * SCI interrupt level register
	 */
	public static final int QDSCI_IL = Kernel.UIMB + 0x5004;
	/**
	 * SPI interrupt level register
	 */
	public static final int QSPI_IL = Kernel.UIMB + 0x5006;

	/**
	 * SCI1 control register 0
	 */
	public static final int SCC1R0 = Kernel.UIMB + 0x5008;
	/**
	 * SCI1 control register 1
	 */
	public static final int SCC1R1 = Kernel.UIMB + 0x500A;
	/**
	 * SCI1 status register
	 */
	public static final int SC1SR = Kernel.UIMB + 0x500C;
	/**
	 * SCI1 data register
	 */
	public static final int SC1DR = Kernel.UIMB + 0x500E;

	/**
	 * QSMCM portQ data register
	 */
	public static final int PORTQS = Kernel.UIMB + 0x5014;
	/**
	 * QSMCM pin assignment register
	 */
	public static final int PQSPAR = Kernel.UIMB + 0x5016;
	/**
	 * QSMCM data direction register
	 */
	public static final int DDRQS = Kernel.UIMB + 0x5017;

	/**
	 * QSPI control register 0
	 */
	public static final int SPCR0 = Kernel.UIMB + 0x5018;
	/**
	 * QSPI control register 1
	 */
	public static final int SPCR1 = Kernel.UIMB + 0x501A;
	/**
	 * QSPI control register 2
	 */
	public static final int SPCR2 = Kernel.UIMB + 0x501C;
	/**
	 * QSPI control register 2
	 */
	public static final int SPCR3 = Kernel.UIMB + 0x501E;
	/**
	 * QSPI status register
	 */
	public static final int SPSR = Kernel.UIMB + 0x501F;

	/**
	 * SCI2 control register 0
	 */
	public static final int SCC2R0 = Kernel.UIMB + 0x5020;
	/**
	 * SCI2 control register 1
	 */
	public static final int SCC2R1 = Kernel.UIMB + 0x5022;
	/**
	 * SCI2 status register
	 */
	public static final int SC2SR = Kernel.UIMB + 0x5024;
	/**
	 * SCI2 data register
	 */
	public static final int SC2DR = Kernel.UIMB + 0x5026;

	/**
	 * QSCI1 control register
	 */
	public static final int QSCI1CR = Kernel.UIMB + 0x5028;
	/**
	 * QSCI1 status register
	 */
	public static final int QSCI1SR = Kernel.UIMB + 0x502A;
	/**
	 * Transmit queue locations
	 */
	public static final int SCTQ = Kernel.UIMB + 0x502C;
	/**
	 * Receive queue locations
	 */
	public static final int SCRQ = Kernel.UIMB + 0x504C;

	/**
	 * QSPI Receive RAM
	 */
	public static final int RR = Kernel.UIMB + 0x5140;
	/**
	 * QSPI Transmit RAM
	 */
	public static final int TR = Kernel.UIMB + 0x5180;
	/**
	 * QSPI Command RAM
	 */
	public static final int CR = Kernel.UIMB + 0x51C0;

	// SCI2 control register 1 (SCC+R+) flags

	/**
	 * SCI1 control register 1 (SCC1R1) flags.<br>
	 * 0 normal operation <br>
	 * 1 break frame(s) transmitted after completion of current frame<br>
	 */
	public static final int scc1r1SBK = 0;

	/**
	 * Receiver Wakeup.<br>
	 * 0 normal receiver operation (received data recognized)<br>
	 * 1 Wakeup mode enabled (received data ignored until awakened)
	 */
	public static final int scc1r1RWU = 1;

	/**
	 * Receiver Enable.<br>
	 * 0 SCI receiver disabled (status bits inhibited)<br>
	 * 1 SCI receiver enabled <br>
	 */
	public static final int scc1r1RE = 2;

	/**
	 * Transmitter Enable.<br>
	 * 0 SCI transmitter disabled (TXD pin can be used as I/O)<br>
	 * 1 SCI transmitter enabled (TXD pin dedicated to SCI transmitter)<br>
	 */
	public static final int scc1r1TE = 3;

	/**
	 * Idle-Line Interrupt Enable.<br>
	 * 0 SCI IDLE interrupts inhibited <br>
	 * 1 SCI IDLE interrupts enabled <br>
	 */
	public static final int scc1rILIE = 4;

	/**
	 * Receiver Interrupt Enable.<br>
	 * 0 SCI RDRF and OR interrupts inhibited<br>
	 * 1 SCI RDRF and OR interrupts enabled<br>
	 */
	public static final int scc1r1RIE = 5;

	/**
	 * Transmit Complete Interrupt Enable.<br>
	 * 0 SCI TC interrupts inhibited<br>
	 * 1 SCI TC interrupts enabled<br>
	 */
	public static final int scc1r1TCIE = 6;

	/**
	 * Transmit Interrupt Enable.<br>
	 * 0 SCI TDRE interrupts inhibited<br>
	 * 1 SCI TDRE interrupts enabled<br>
	 */
	public static final int scc1r1TIE = 7;

	/**
	 * Wakeup by Address Mark.<br>
	 * 0 SCI receiver awakened by idle-line detection<br>
	 * 1 SCI receiver awakened by address mark (last bit set) <br>
	 */
	public static final int scc1r1WAKE = 8;

	/**
	 * Mode Select. <br>
	 * 0 10-bit SCI frame <br>
	 * 1 11-bit SCI frame<br>
	 */
	public static final int scc1r1M = 9;

	/**
	 * Parity Enable.<br>
	 * 0 SCI parity disabled<br>
	 * 1 SCI parity enabled<br>
	 */
	public static final int scc1r1PE = 10;

	/**
	 * Parity Type. <br>
	 * 0 even parity<br>
	 * 1 odd parity <br>
	 */
	public static final int scc1r1PT = 11;

	/**
	 * Idle-Line Detect Type.<br>
	 * 0 Short idle-line detect (start count on first one) <br>
	 * 1 Long idle-line detect (start count on first one after stop bit(s)) <br>
	 */
	public static final int scc1r1ILT = 12;

	/**
	 * Wired-OR Mode for SCI Pins.<br>
	 * 0 If configured as an output, TXD is a normal CMOS output <br>
	 * 1 If configured as an output, TXD is an open-drain output <br>
	 */
	public static final int scc1r1WOMS = 13;

	/**
	 * Loop Mode.<br>
	 * 0 normal SCI operation, no looping, feedback path disabled<br>
	 * 1 Test SCI operation, looping, feedback path enabled<br>
	 */
	public static final int scc1r1LOOPS = 14;

	// SCI1 status register (SC1SR) Flags

	/**
	 * Parity Error. <br>
	 * 0 No parity error on the received data <br>
	 * 1 Parity error ocured on the received data <br>
	 */
	public static final int sc1srPF = 0;

	/**
	 * Framing Error.<br>
	 * 0 No framing error on the received data <br>
	 * 1 Framing error or break ocured on the received data<br>
	 */
	public static final int sc1srFE = 1;

	/**
	 * Noise Error Flag.<br>
	 * 0 No noise detectedon the received data <br>
	 * 1 Noise ocured on the received data<br>
	 */
	public static final int sc1srNF = 2;

	/**
	 * Overrun Error. <br>
	 * 0 RDRF is cleare before new data arrives<br>
	 * 1 RDRF is not cleare before new data arrives<br>
	 */
	public static final int sc1srOR = 3;

	/**
	 * Idle-Line detected. <br>
	 * 0 SCI receiver did not detect an idle-line condition<br>
	 * 1 SCI receiver detected an idle-line condition<br>
	 */
	public static final int sc1srIDLE = 4;

	/**
	 * Receiver Active.<br>
	 * 0 SCI receiver is idle<br>
	 * 1 SCI receiver is busy<br>
	 */
	public static final int sc1srRAF = 5;

	/**
	 * Receiver Data Register Full. <br>
	 * 0 Register RDR is empty or contains previously read data <br>
	 * 1 Register RDR contains new data<br>
	 */
	public static final int sc1srRDRF = 6;

	/**
	 * Transmit Complete.<br>
	 * 0 SCI transmitter is busy <br>
	 * 1 SCI transmitter is idle<br>
	 */
	public static final int sc1srTC = 7;

	/**
	 * Transmit Data Register Empty.<br>
	 * 0 Register TDR still contains data to be sent to the transmit serial
	 * shifter<br>
	 * 1 A new character can now be written to register TDR<br>
	 */
	public static final int sc1srTDRE = 8;

	// SCI1 control register 1 (SCC+R+) flags

	/**
	 * SCI1 control register 1 (scc2R1) flags.<br>
	 * 0 normal operation <br>
	 * 1 break frame(s) transmitted after completion of current frame<br>
	 */
	public static final int scc2r1SBK = 0;

	/**
	 * Receiver Wakeup.<br>
	 * 0 normal receiver operation (received data recognized)<br>
	 * 1 Wakeup mode enabled (received data ignored until awakened)
	 */
	public static final int scc2r1RWU = 1;

	/**
	 * Receiver Enable.<br>
	 * 0 SCI receiver disabled (status bits inhibited)<br>
	 * 1 SCI receiver enabled <br>
	 */
	public static final int scc2r1RE = 2;

	/**
	 * Transmitter Enable.<br>
	 * 0 SCI transmitter disabled (TXD pin can be used as I/O)<br>
	 * 1 SCI transmitter enabled (TXD pin dedicated to SCI transmitter)<br>
	 */
	public static final int scc2r1TE = 3;

	/**
	 * Idle-Line Interrupt Enable.<br>
	 * 0 SCI IDLE interrupts inhibited <br>
	 * 1 SCI IDLE interrupts enabled <br>
	 */
	public static final int scc2r1ILIE = 4;

	/**
	 * Receiver Interrupt Enable.<br>
	 * 0 SCI RDRF and OR interrupts inhibited<br>
	 * 1 SCI RDRF and OR interrupts enabled<br>
	 */
	public static final int scc2r1RIE = 5;

	/**
	 * Transmit Complete Interrupt Enable.<br>
	 * 0 SCI TC interrupts inhibited<br>
	 * 1 SCI TC interrupts enabled<br>
	 */
	public static final int scc2r1TCIE = 6;

	/**
	 * Transmit Interrupt Enable.<br>
	 * 0 SCI TDRE interrupts inhibited<br>
	 * 1 SCI TDRE interrupts enabled<br>
	 */
	public static final int scc2r1TIE = 7;

	/**
	 * Wakeup by Address Mark.<br>
	 * 0 SCI receiver awakened by idle-line detection<br>
	 * 1 SCI receiver awakened by address mark (last bit set) <br>
	 */
	public static final int scc2r1WAKE = 8;

	/**
	 * Mode Select. <br>
	 * 0 10-bit SCI frame <br>
	 * 1 11-bit SCI frame<br>
	 */
	public static final int scc2r1M = 9;

	/**
	 * Parity Enable.<br>
	 * 0 SCI parity disabled<br>
	 * 1 SCI parity enabled<br>
	 */
	public static final int scc2r1PE = 10;

	/**
	 * Parity Type. <br>
	 * 0 even parity<br>
	 * 1 odd parity <br>
	 */
	public static final int scc2r1PT = 11;

	/**
	 * Idle-Line Detect Type.<br>
	 * 0 Short idle-line detect (start count on first one) <br>
	 * 1 Long idle-line detect (start count on first one after stop bit(s)) <br>
	 */
	public static final int scc2r1ILT = 12;

	/**
	 * Wired-OR Mode for SCI Pins.<br>
	 * 0 If configured as an output, TXD is a normal CMOS output <br>
	 * 1 If configured as an output, TXD is an open-drain output <br>
	 */
	public static final int scc2r1WOMS = 13;

	/**
	 * Loop Mode.<br>
	 * 0 normal SCI operation, no looping, feedback path disabled<br>
	 * 1 Test SCI operation, looping, feedback path enabled<br>
	 */
	public static final int scc2r1LOOPS = 14;

	// SCI1 status register (sc2SR) Flags

	/**
	 * Parity Error. <br>
	 * 0 No parity error on the received data <br>
	 * 1 Parity error ocured on the received data <br>
	 */
	public static final int sc2srPF = 0;

	/**
	 * Framing Error.<br>
	 * 0 No framing error on the received data <br>
	 * 1 Framing error or break ocured on the received data<br>
	 */
	public static final int sc2srFE = 1;

	/**
	 * Noise Error Flag.<br>
	 * 0 No noise detectedon the received data <br>
	 * 1 Noise ocured on the received data<br>
	 */
	public static final int sc2srNF = 2;

	/**
	 * Overrun Error.<br>
	 * 0 RDRF is cleare before new data arrives<br>
	 * 1 RDRF is not cleare before new data arrives<br>
	 */
	public static final int sc2srOR = 3;

	/**
	 * Idle-Line detected. <br>
	 * 0 SCI receiver did not detect an idle-line condition<br>
	 * 1 SCI receiver detected an idle-line condition<br>
	 */
	public static final int sc2srIDLE = 4;

	/**
	 * Receiver Active.<br>
	 * 0 SCI receiver is idle<br>
	 * 1 SCI receiver is busy<br>
	 */
	public static final int sc2srRAF = 5;

	/**
	 * Receiver Data Register Full.<br>
	 * 0 Register RDR is empty or contains previously read data <br>
	 * 1 Register RDR contains new data<br>
	 */
	public static final int sc2srRDRF = 6;

	/**
	 * Transmit Complete.<br>
	 * 0 SCI transmitter is busy <br>
	 * 1 SCI transmitter is idle<br>
	 */
	public static final int sc2srTC = 7;

	/**
	 * Transmit Data Register Empty.<br>
	 * 0 Register TDR still contains data to be sent to the transmit serial
	 * shifter<br>
	 * 1 A new character can now be written to register TDR<br>
	 */
	public static final int sc2srTDRE = 8;
	
	
	/**
	 * Call this method to initialize the interrupt levels if you use SPI or SCI interrupts.
	 */
	public static void init(){}

	static {
		SYS.PUT2(QSMCM_REG,0);
		SYS.PUT2(QDSCI_IL, SCI_IntLevel * 0x100);
		SYS.PUT2(QSPI_IL, SPI_IntLevel);
	}
}