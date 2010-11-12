package ch.ntb.inf.sts.mpc555;

interface Registers {
	// internal memory 
	int ISB	= 0;	// internal memory base 

	// USIU & Flash control register 
	// start of internal memory map depends on hard reset configuration 
	int USIU = ISB + 0x002FC000;	// unified system interface unit 
	int SIUMCR = USIU;	// SIU configuration 
	int UIMB = USIU + 0x4000;
	int UMCR = UIMB + 0x7F80;
/*	SYPCR	 = USIU + 4; 	(* system protection control *)
	SWSR	 = USIU + 0EH; 	(* system service *)
	SIPEND	 = USIU + 10H; 	(* interrupt pending *)
	SIMASK	 = USIU + 14H; 	(* interrupt mask *)
	SIEL	 = USIU + 18H; 	(* interrupt edge level mask *)
	SIVEC	 = USIU + 1CH; 	(* interrupt vector *)
	TESR	 = USIU + 20H; 	(* transfer error status *)
	SGPIODT1	 = USIU + 24H; 	(* USIU general-purpose I/O data 1 *)
	SGPIODT2	 = USIU + 28H; 	(* USIU general-purpose I/O data 2 *)
	SGPIOCR	 = USIU + 2CH; 	(* USIU general-purpose I/O control *)
	EMCR	 = USIU + 30H; 	(* external master mode control *)*/
	int PDMCR	 = USIU + 0x3C;	// pads module configuration *)

	int BR0	 = USIU + 0x100; 	// base register 0 *)
	int OR0	 = USIU + 0x104; 	// option register 0 *)
	int BR1	 = USIU + 0x108; 	// base register 1 *)
	int OR1	 = USIU + 0x10C; 	// option register 1 *)
/*	BR2*	 = USIU + 110H; 	(* base register 2 *)
	OR2*	 = USIU + 114H; 	(* option register 2 *)
	BR3*	 = USIU + 118H; 	(* base register 3 *)
	OR3*	 = USIU + 11CH; 	(* option register 3 *)

	DMBR	 = USIU + 140H; 	(* dual mapping base register *)
	DMOR	 = USIU + 144H; 	(* dual mapping option register *)
	MSTAT	 = USIU + 178H; 	(* memory status *)*/

	int TBSCR	 = USIU + 0x200; 	// time base status and control *)
/*	TBREF0	 = USIU + 204H; 	(* time base reference 0 *)
	TBREF1	 = USIU + 208H; 	(* time base reference 1 *)
	RTCSC	 = USIU + 220H; 	(* real time clock status and control *)
	RTC	 = USIU + 224H; 	(* real time clock *)
	RTCEC	 = USIU + 228H; 	(* real time alarm seconds *)
	RTCAL	 = USIU + 22CH; 	(* real time alarm *)
	PISCR	 = USIU + 240H; 	(* PIT status and control *)
	PITC	 = USIU + 244H; 	(* PIT count *)
	PITR	 = USIU + 248H; 	(* PIT register *)*/

	int SCCR = USIU + 0x280;	// system clock control 
	int PLPRCR = USIU + 0x284;	// PLL, low-power, reset control 
	int RSR = USIU + 0x288;	// reset status 
	int COLIR = USIU + 0x28C;	// change of lock interrupt 
	int VSRMCR = USIU + 0x290;	// VDDSRAM control 

}
