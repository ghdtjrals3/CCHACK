// /static/js/auth-upload.js
export function bindUploadModal() {
  const uploadModal=document.getElementById('uploadModal');
  const uTitle=document.getElementById('uMissionTitle');
  const uPoints=document.getElementById('uPoints');
  const uFile=document.getElementById('uFile');
  const uPreview=document.getElementById('uPreview');
  const uPreviewImg=uPreview.querySelector('img');
  const uMemo=document.getElementById('uMemo');
  const uSubmit=document.getElementById('uSubmit');
  const dropzone=document.getElementById('dropzone');

  function openUploadModal(m,p){
    uTitle.textContent=m.title; uPoints.textContent=`+ ${p}p`;
    uFile.value=''; uMemo.value=''; uPreview.style.display='none'; uPreviewImg.src='';
    uploadModal.classList.add('open'); lockScroll(true);
  }
  function closeUploadModal(){ uploadModal.classList.remove('open'); lockScroll(false); }

  function lockScroll(lock){ document.documentElement.style.overflow = lock ? 'hidden' : ''; }
  document.querySelectorAll('[data-close="upload"]').forEach(el=>el.onclick=closeUploadModal);

  uFile.addEventListener('change', ()=>{
    const f=uFile.files && uFile.files[0];
    if(!f){ uPreview.style.display='none'; uPreviewImg.src=''; return; }
    const url=URL.createObjectURL(f); uPreviewImg.src=url; uPreview.style.display='block';
  });
  ['dragenter','dragover'].forEach(ev=>{
    dropzone.addEventListener(ev, e=>{ e.preventDefault(); dropzone.style.borderColor='#b7c7ff'; });
  });
  ['dragleave','drop'].forEach(ev=>{
    dropzone.addEventListener(ev, e=>{ e.preventDefault(); dropzone.style.borderColor='#cfd6e3'; });
  });
  dropzone.addEventListener('drop', e=>{
    const f=e.dataTransfer.files && e.dataTransfer.files[0]; if(!f) return;
    uFile.files=e.dataTransfer.files;
    const url=URL.createObjectURL(f); uPreviewImg.src=url; uPreview.style.display='block';
  });
  uSubmit.onclick=()=>{ alert('업로드 완료! 포인트가 적립됩니다.'); closeUploadModal(); };

  // 외부에서 쓰도록 공개
  return { openUploadModal, closeUploadModal };
}
