- name: Create the jenkins home directory inside the EFS
  script: "./jaas-install.sh --efs_root_dir {{ efs_root_dir }}"
  args:
    creates: "{{ efs_root_dir }}/jenkins_home"
