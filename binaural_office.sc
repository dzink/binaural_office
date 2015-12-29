s.boot;

(
play {
	var in = SoundIn.ar([0,1]);
	var bpfLfo = SinOsc.kr(LFNoise2.kr([0.5, 0.55]).linexp(-1, 1, 0.15, 0.75));
	var delayLfo = LFNoise2.kr(LFNoise1.kr([2,2]).linexp(-1, 1, 0.15, 0.75));
	var delayTime = 0.3 * pow(2, delayLfo.range(0.1, 0.9) * MouseX.kr(0.25,4,1,0.2));
	var pulseTime = Demand.kr(Dust.kr([1, 1]), 0, Dwhite.new()).round(0.125).linexp(0, 1, 1, 16);
	var low, hi;


	//in = Trig1.kr(Dust.kr(4, 1), [0.04,0.04]) * Pulse.ar(bpfLfo.range(40,30).midicps) * 0.1;
	//in = FreeVerb.ar(in, 0.75, 0.5, 0);
	// pull sound in from internal microphones, process to remove air hiss
	in = SoundIn.ar([0,1]);
	in = CompanderD.ar(in, 0.0001, 10, 1, 0.01, 0.001) * 20;

	// two stereo feedback delay lines, and then a low/high crossover
	in = in + CombC.ar(in, 1, delayLfo.linexp(-1, 1, 0.1, 0.9), 1.5);
	in = in + CombC.ar(in, 1, delayTime,1);
	low = LPF.ar(in, 40.midicps, 0.5);
	hi = BPF.ar(in, bpfLfo.range(60, 100).midicps, 0.1);


	in = in + low + hi;
	// add a -5 half step pitch shift, then a chirping grain delay, verb, etc
	in = PitchShift.ar(in, 0.01, 2/3) * 3 + in;
	in = Pan2.ar(PitchShift.ar(Mix.ar(in), 0.75, 10.5), LFSaw.kr(LFNoise0.kr(1).range(-1,1))) + in;
	in = in + (FreeVerb.ar(in, 1, 1) * LFPulse.kr(pulseTime));

	// binaural panning chaos, then clean up the loud bits
	in = Pan2.ar(in, bpfLfo.range([-1, 1], [1, -1]));
	in = Compander.ar(in, 0.1, 1, 0.1);
	in.softclip * 0.01;
	in * 0.1
};
);
