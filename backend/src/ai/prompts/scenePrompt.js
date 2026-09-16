export function buildScenePrompt(input) {
  return `
حوّل الفكرة التالية إلى ${input.sceneCount} مشاهد
لفيديو قصير.

الفكرة:
${input.idea}

اللغة:
${input.language}

القالب:
${input.template}

مقاس الفيديو:
${input.aspectRatio}

المدة الكلية:
${input.durationSeconds} ثانية

الشروط:
- اكتب باللغة المطلوبة.
- اجعل narration مناسبًا للتعليق الصوتي.
- اجعل visualPrompt وصفًا بصريًا واضحًا.
- اجعل المشاهد مترابطة.
- اجعل position يبدأ من صفر.
- اجعل مجموع مدد المشاهد قريبًا من المدة الكلية.
- أعد JSON فقط.
`;
}
