/* ==========================================================================
   EquipTrust — Institutional Resource & Equipment Sharing Platform
   Complete Application Logic (script.js)
   ========================================================================== */

const STORAGE_KEY = "equiptrust_hackathon_state_v1";

// 1. CERTIFICATION DEFINITIONS
const CERTIFICATIONS = {
  NONE: {
    id: "NONE",
    name: "General Campus Access",
    desc: "Basic institutional ID verification. No specialized hazard training required.",
    quizQuestion: "Do you agree to follow standard room occupancy and cleanliness rules?"
  },
  CERT_3D: {
    id: "CERT_3D",
    name: "3D Printing & Rapid Prototyping L1",
    desc: "Covers heated bed safety, filament loading, nozzle jam prevention, and post-print removal.",
    quizQuestion: "Safety Check: Before removing a finished 3D print from the build plate, you must verify the bed temperature has cooled below 40°C."
  },
  CERT_LASER: {
    id: "CERT_LASER",
    name: "Laser & CNC High-Power Safety L2",
    desc: "Covers Class-4 laser containment, exhaust ventilation, fire extinguisher location, and banned PVC materials.",
    quizQuestion: "Safety Check: Never cut PVC or vinyl in a laser cutter because it releases toxic and corrosive chlorine gas."
  },
  CERT_BIO: {
    id: "CERT_BIO",
    name: "Bio-Imaging & Cleanroom Protocol",
    desc: "Covers optical lens handling, oil immersion cleaning, sample containment, and anti-static grounding.",
    quizQuestion: "Safety Check: Always lower the microscope stage and switch to the lowest objective lens before removing a slide."
  },
  CERT_MEDIA: {
    id: "CERT_MEDIA",
    name: "Pro Audio/Cinema Gear Handling",
    desc: "Covers CMOS sensor protection, battery storage, tripod locking, and acoustic studio etiquette.",
    quizQuestion: "Safety Check: Never point an exposed cinema camera sensor directly toward laser lights or direct sunlight."
  }
};

// 2. INITIAL DEMO DATA FACTORY
function getInitialDemoData() {
  const todayStr = new Date().toISOString().split("T")[0];

  return {
    activeUserId: "user_aarav",

    users: [
      {
        id: "user_aarav",
        name: "Aarav Sharma",
        initials: "AS",
        role: "Verified Student",
        dept: "B.Tech Mechanical Eng • ID: INST-2026-884",
        isManager: false,
        weeklyMaxHours: 6,
        fairScore: 92,
        certs: ["NONE", "CERT_3D", "CERT_MEDIA"]
      },
      {
        id: "user_priya",
        name: "Priya Patel",
        initials: "PP",
        role: "First-Year Creator",
        dept: "B.Des Industrial Design • ID: INST-2026-109",
        isManager: false,
        weeklyMaxHours: 6,
        fairScore: 98,
        certs: ["NONE"] // Needs to earn certifications to book advanced gear!
      },
      {
        id: "user_vikram",
        name: "Dr. Vikram Nair",
        initials: "VN",
        role: "Postdoc Researcher",
        dept: "Center for Nano & Bio Materials • ID: FAC-2026-312",
        isManager: false,
        weeklyMaxHours: 6,
        fairScore: 95,
        certs: ["NONE", "CERT_3D", "CERT_LASER", "CERT_BIO", "CERT_MEDIA"]
      },
      {
        id: "manager_meera",
        name: "Prof. Meera Krishnan",
        initials: "MK",
        role: "Lab Manager & Resource Owner",
        dept: "Central Makerspace & Instrumentation Governance",
        isManager: true,
        weeklyMaxHours: 6,
        fairScore: 100,
        certs: ["NONE", "CERT_3D", "CERT_LASER", "CERT_BIO", "CERT_MEDIA"]
      }
    ],

    resources: [
      {
        id: "res_101",
        name: "Ultimaker S5 Pro Dual-Extrusion 3D Printer",
        category: "Fabrication & Makerspace",
        emoji: "🖨️",
        visualClass: "visual-fab",
        ownerDept: "Central Makerspace Lab",
        location: "Innovation Block A • Room 104",
        requiredCert: "CERT_3D",
        maxHours: 2,
        deposit: "₹500 Refundable Hold (Free Academic Use)",
        status: "AVAILABLE",
        lastCondition: "Excellent — Calibrated & Clean",
        rules: "PLA/Tough PLA/PVA only. User must watch first 15 mins of print layer adhesion and clean build plate after completion."
      },
      {
        id: "res_102",
        name: "Epilog Fusion Pro 80W CO2 Laser Cutter",
        category: "Fabrication & Makerspace",
        emoji: "⚡",
        visualClass: "visual-fab",
        ownerDept: "Mechanical Prototyping Facility",
        location: "Engineering Workshop • Bay 2",
        requiredCert: "CERT_LASER",
        maxHours: 2,
        deposit: "₹1,000 Refundable Safety Hold",
        status: "AVAILABLE",
        lastCondition: "Excellent — Optics Cleaned",
        rules: "Mandatory air-assist & exhaust fan activation. Strictly NO PVC, polycarbonate, or unknown plastics. Never leave unattended."
      },
      {
        id: "res_103",
        name: "Zeiss Axio Observer Fluorescence Microscope",
        category: "Scientific & Lab",
        emoji: "🔬",
        visualClass: "visual-lab",
        ownerDept: "Department of Biosciences",
        location: "Research Wing C • Cleanroom 302",
        requiredCert: "CERT_BIO",
        maxHours: 2,
        deposit: "Faculty Endorsement Required",
        status: "AVAILABLE",
        lastCondition: "Good — 63x Lens Cleaned",
        rules: "Book only for prepared non-pathogenic slides. Log UV lamp hours accurately upon check-out."
      },
      {
        id: "res_104",
        name: "Sony FX6 Full-Frame Cinema Rig & Gimbal Kit",
        category: "Media & Production",
        emoji: "🎥",
        visualClass: "visual-media",
        ownerDept: "Digital Media & Film Society",
        location: "Creative Hub • Locker B12",
        requiredCert: "CERT_MEDIA",
        maxHours: 4,
        deposit: "₹1,500 Security Deposit Hold",
        status: "AVAILABLE",
        lastCondition: "Excellent — 2 V-Mount Batteries Charged",
        rules: "Inspect lens glass before and after checkout. Format media cards prior to return and place batteries on charger."
      },
      {
        id: "res_105",
        name: "Acoustic Podcast & Foley Recording Studio",
        category: "Specialized Space",
        emoji: "🎙️",
        visualClass: "visual-space",
        ownerDept: "Student Media Center",
        location: "Student Activity Center • Room 210",
        requiredCert: "NONE",
        maxHours: 2,
        deposit: "Free Campus Community Access",
        status: "AVAILABLE",
        lastCondition: "Excellent — 4 Shure SM7B Mics Ready",
        rules: "No food or uncovered drinks near mixing console. Return phantom power switches to OFF before check-out."
      },
      {
        id: "res_106",
        name: "5-Axis Desktop CNC Milling Machine",
        category: "Fabrication & Makerspace",
        emoji: "⚙️",
        visualClass: "visual-fab",
        ownerDept: "Robotics & Mechatronics Lab",
        location: "Innovation Block A • Room 108",
        requiredCert: "CERT_LASER",
        maxHours: 2,
        deposit: "₹1,000 Tooling Hold",
        status: "MAINTENANCE",
        lastCondition: "Flagged — Spindle collet inspection pending",
        rules: "G-code toolpath simulation must be verified before mounting